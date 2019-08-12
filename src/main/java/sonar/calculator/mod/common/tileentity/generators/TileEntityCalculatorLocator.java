package sonar.calculator.mod.common.tileentity.generators;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.api.items.ILocatorModule;
import sonar.calculator.mod.api.machines.ICalculatorLocator;
import sonar.calculator.mod.common.block.generators.CalculatorLocator;
import sonar.core.SonarCore;
import sonar.core.common.tileentity.TileEntityInventorySender;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.utils.IByteBufTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCalculatorLocator extends TileEntityInventorySender implements IByteBufTile, ICalculatorLocator {

	public SyncTagType.BOOLEAN active = new SyncTagType.BOOLEAN(0);
	public SyncTagType.INT size = new SyncTagType.INT(1);
	public SyncTagType.INT stability = new SyncTagType.INT("stability");
	public SyncTagType.STRING owner = (STRING) new SyncTagType.STRING("owner").setDefault("None");
	private int sizeTicks, luckTicks;
	
	public static double configMultiplier = CalculatorConfig.getDouble(CalculatorConfig.locatorCategory + CalculatorConfig.locatorOutputMultiplierMsg);

	public TileEntityCalculatorLocator() {
		super.storage = new SyncEnergyStorage((int) (configMultiplier * 25000000), (int) (configMultiplier * 128000));
		super.slots = new ItemStack[2];
		super.maxTransfer = (int) (configMultiplier * 100000);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			boolean invert = false;
			if (canGenerate()) {
				beginGeneration();
				if (!active.getObject()) {
					invert = true;
				}
			} else if (active.getObject()) {
				invert = true;
			}
			if (invert) {
				this.active.invert();
				SonarCore.sendPacketAround(this, 128, 0);
			}

			if (!(this.sizeTicks >= 25)) {
				sizeTicks++;
			} else {
				this.sizeTicks = 0;
				this.createStructure();
				this.getStability();
			}
		}
		this.charge(0);
		this.addEnergy();
		this.markDirty();
	}

	public int currentOutput() {
		int size = this.size.getObject();
		if (size != 0 && (((int) (2 * size + 1) * (2 * size + 1)) - 1) != 0) {
			int stable = (int) (stability.getObject() * 100) / ((int) (2 * size + 1) * (2 * size + 1));
			return (int)(configMultiplier * (((5 + ((int) (1000 * (Math.sqrt(size * 1.8)) - 100 * (Math.sqrt(100 - stable))) / (int) (11 - Math.sqrt(stable))) * size))*2));
		}
		return 0;
	}

	public void getStability() {
		int currentStable = 0;
		if (size.getObject() == 0) {
			this.stability.setObject(0);
		}

		for (int Z = -(size.getObject()); Z <= (size.getObject()); Z++) {
			for (int X = -(size.getObject()); X <= (size.getObject()); X++) {
				TileEntity target = this.worldObj.getTileEntity(xCoord + X, yCoord, zCoord + Z);
				if (target != null && target instanceof TileEntityCalculatorPlug) {
					TileEntityCalculatorPlug plug = (TileEntityCalculatorPlug) target;
					currentStable += plug.getS();
				}
			}
		}
		this.stability.setObject(currentStable);
	}

	public boolean canGenerate() {
		if (!(this.storage.getEnergyStored() < this.storage.getMaxEnergyStored()) || size.getObject() == 0) {
			return false;
		}
		if (isLocated()) {
			if (this.stability.getObject() >= 7) {
				return true;
			} else {
				EntityPlayer player = this.worldObj.getPlayerEntityByName(getOwner());
				if (player != null) {
					return true;
				}
			}
		}

		return false;
	}

	public void beginGeneration() {
		storage.modifyEnergyStored(currentOutput());
		if (!this.worldObj.isRemote) {
			if (this.luckTicks >= 0 && this.luckTicks != 50) {
				this.luckTicks++;
			} else if (this.luckTicks == 50) {
				this.luckTicks = 0;
				this.effectStart();
			}
			if (CalculatorConfig.timeEffect) {
				if (stability.getObject() * 4 < 20) {
					this.timeStart();
				}
			}
		}
	}

	private void timeStart() {
		this.worldObj.setWorldTime(worldObj.getWorldTime() + 100);
	}

	private void addEnergy() {
		TileEntity entity = this.worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
		if (SonarHelper.isEnergyHandlerFromSide(entity, ForgeDirection.DOWN)) {
			this.storage.extractEnergy(SonarHelper.pushEnergy(entity, ForgeDirection.UP, this.storage.extractEnergy(maxTransfer, true), false), false);
		}
	}

	public void addItem(EntityPlayer player, Item item) {
		player.inventory.addItemStackToInventory(new ItemStack(item));
	}

	public void addPotion(EntityPlayer player, int potionID) {
		player.addPotionEffect(new PotionEffect(potionID, 1000, 1));
	}

	private void effectStart() {
		EntityPlayer player = this.worldObj.getPlayerEntityByName(getOwner());
		if (player != null) {
			double x = player.posX;
			double y = player.posY;
			double z = player.posZ;
			int stability = this.stability.getObject();
			int luck = 1 + (int) (Math.random() * ((20 * (stability + 1) - 1) + 20 * (stability + 1)));

			if (stability == 0) {
				worldObj.createExplosion(player, x, y, z, 4F, true);
				player.setHealth(player.getHealth() - 4);

			} else {
				if (stability < 2) {
					switch (luck) {
					case 1:
						worldObj.createExplosion(player, x, y, z, 4F, true);
						player.setHealth(player.getHealth() - 4);
						break;
					}
				}
				if (stability < 4) {
					switch (luck) {
					case 2:
						player.setFire(400);
						break;
					case 3:
						worldObj.createExplosion(player, x, y, z, 8F, true);
						break;
					case 4:
						worldObj.createExplosion(player, x, y, z, 6F, true);
						break;
					case 5:
						addPotion(player, Potion.confusion.id);
						break;
					case 6:
						addPotion(player, Potion.blindness.id);
						break;
					}
				}
				if (stability < 6) {
					switch (luck) {
					case 7:
						addItem(player, Calculator.grenade);
						break;
					case 8:
						addItem(player, Calculator.itemScientificCalculator);
						break;
					case 9:
						addItem(player, Calculator.itemCalculator);
						break;
					case 10:
						addPotion(player, Potion.jump.id);
						break;
					case 11:
						addPotion(player, Potion.waterBreathing.id);
						break;
					case 12:
						addPotion(player, Potion.digSlowdown.id);
						break;
					case 13:
						addPotion(player, Potion.damageBoost.id);
						break;
					}
				}
				if (stability < 7) {
					switch (luck) {
					case 14:
						addPotion(player, Potion.wither.id);
						break;
					case 15:
						addItem(player, Items.milk_bucket);
						break;
					case 16:
						if (luck == 16) {
							int luck2 = 1 + (int) (Math.random() * ((5) - 1) + 5);
							if (luck2 == 16) {
								worldObj.createExplosion(player, x, y, z, 80F, true);
								player.setHealth(player.getHealth() - 40);
							}
						}
						break;
					}
				}
			}
		}
	}

	protected boolean isLocated() {
		ItemStack stack = this.getStackInSlot(1);
		if (stack == null) {
			return false;
		}
		if (stack.getItem() instanceof ILocatorModule) {
			String name = ((ILocatorModule) stack.getItem()).getPlayer(stack);
			if (name != null && !name.equals("None")) {
				return true;
			}
		}

		return false;

	}

	public void createOwner() {
		ItemStack stack = this.getStackInSlot(1);
		if (stack == null) {
			this.owner.setObject("None");
			return;
		}

		if (stack.getItem() instanceof ILocatorModule) {
			String name = ((ILocatorModule) stack.getItem()).getPlayer(stack);
			if (name != null) {
				this.owner.setObject(name);
			}
		}

	}

	public void createStructure() {
		int size = CalculatorLocator.multiBlockStructure(getWorldObj(), xCoord, yCoord, zCoord);
		if (size != this.size.getObject()) {
			this.size.setObject(size);
			SonarCore.sendPacketAround(this, 128, 1);
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			this.luckTicks = nbt.getInteger("ticks");
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			nbt.setInteger("ticks", this.luckTicks);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.add(active);
		parts.add(size);
		parts.add(stability);
		parts.add(owner);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		if (from == ForgeDirection.DOWN) {
			return true;
		}
		return false;
	}

	public boolean maxRender() {
		return true;
	}

	public int beamHeight() {
		int f = 0;
		for (int i = 1; i <= 256; i++) {
			if (this.worldObj.isSideSolid(xCoord, yCoord + i, zCoord, ForgeDirection.UP)) {
				return i;
			}
		}
		return 256;
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		currenttip.add(FontHelper.translate("locator.active") + ": " + (!active.getObject() ? FontHelper.translate("locator.false") : FontHelper.translate("locator.true")));
		currenttip.add(FontHelper.translate("locator.owner") + ": " + (!owner.getObject().equals("None") ? owner.getObject() : FontHelper.translate("locator.none")));
		return currenttip;
	}

	public void onLoaded() {
		super.onLoaded();
		createOwner();
		createStructure();
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			active.writeToBuf(buf);
		}
		if (id == 1) {
			size.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			active.readFromBuf(buf);
		}
		if (id == 1) {
			size.readFromBuf(buf);
		}
	}

	@Override
	public String getOwner() {
		return owner.getObject();
	}

	@Override
	public int getSize() {
		return size.getObject();
	}

	@Override
	public boolean isActive() {
		return active.getObject();
	}

	@Override
	public double getStabilityPercent() {
		return (stability.getObject() * 100 / (((2 * size.getObject()) + 1) * ((2 * size.getObject()) + 1) - 1));
	}

}