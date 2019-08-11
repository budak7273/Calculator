package sonar.calculator.mod.common.tileentity.machines;

import java.util.List;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.api.machines.IProcessMachine;
import sonar.calculator.mod.utils.AtomicMultiplierBlacklist;
import sonar.core.common.tileentity.TileEntityInventoryReceiver;
import sonar.core.energy.DischargeValues;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncEnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAtomicMultiplier extends TileEntityInventoryReceiver implements ISidedInventory, IProcessMachine {

	public int cookTime, active;
	public int furnaceSpeed = CalculatorConfig.getInteger(CalculatorConfig.multiplierProcessTimeMsg + CalculatorConfig.multiplierCategory);
	public static long requiredEnergy = CalculatorConfig.getLong(CalculatorConfig.multiplierTotalEnergyMsg + CalculatorConfig.multiplierCategory);

	private static final int[] input = new int[] { 0 };
	private static final int[] circuits = new int[] { 1, 2, 3, 4, 5, 6, 7 };
	private static final int[] output = new int[] { 8 };

	public TileEntityAtomicMultiplier() {
		int configCapacity = CalculatorConfig.getInteger(CalculatorConfig.multiplierEnergyStorageMsg + CalculatorConfig.multiplierCategory);
		
		if (configCapacity < (int) getEnergyUsage()) { //ensure that the machine can store at least one tick of energy usage regardless of config
			super.storage = new SyncEnergyStorage((int) getEnergyUsage(), (int) requiredEnergy);
		} else {
			super.storage = new SyncEnergyStorage(configCapacity, (int) requiredEnergy);
		}
		super.slots = new ItemStack[10];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		discharge(9);
		if (this.cookTime > 0) {
			this.active = 1;
			this.cookTime++;
			int energy = (int) getEnergyUsage(); //NOTE - if greater than max int, will be rounded down to max int
			this.storage.modifyEnergyStored(-energy);
		}
		if (this.canCook()) {
			if (!this.worldObj.isRemote) {
				if (cookTime == 0) {
					this.cookTime++;
				}
			}
			if (this.cookTime == furnaceSpeed) {

				this.cookTime = 0;
				this.cookItem();
				this.active = 0;

				int energy = (int) getEnergyUsage(); //NOTE - if greater than max int, will be rounded down to max int
				this.storage.modifyEnergyStored(-energy);
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}

		} else {
			if (this.cookTime != 0 || this.active != 0) {
				this.cookTime = 0;
				this.active = 0;
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		this.markDirty();

	}

	public boolean canCook() {
		if (this.storage.getEnergyStored() == 0) { //must have energy
			return false;
		}
		for (int i = 0; i < 8; i++) { //all 8 slots must be occupied
			if (slots[i] == null) {
				return false;
			}
		}
		if (!isAllowed(slots[0])) { //the first item must be allowed in the slot
			return false;
		}

		if (slots[8] != null) { //has 8 items
			if (slots[8].stackSize + 4 > 64) {
				return false;
			}
			if (!slots[0].isItemEqual(slots[8])) { //8 items must be the same
				return false;
			}
		}

		if (cookTime == 0) { //has not yet begun producing a new round
			/*if (this.storage.getEnergyStored() < requiredEnergy) { //require full buffer to begin process
				return false;
			}*/
			if (this.storage.getEnergyStored() < getEnergyUsage()) { //require at least one operation of energy to start
				return false;
			}
		}
		if (!(slots[0].getMaxStackSize() >= 4)) { //don't allow producing stacks if 4 is greater than the stack size
			return false;
		}

		for (int i = 1; i < 8; i++) { //verify that items are circuits
			if (slots[i].getItem() != Calculator.circuitBoard) {
				return false;
			}
		}

		if (cookTime >= furnaceSpeed) { //if done cooking
			return true;
		}
		return true; //base case of yes

	}

	public static boolean isAllowed(ItemStack stack) {
		return AtomicMultiplierBlacklist.blacklist().isAllowed(stack.getItem());
	}

	private void cookItem() {
		ItemStack itemstack = new ItemStack(slots[0].getItem(), 4, slots[0].getItemDamage());
		if (this.slots[8] == null) {
			this.slots[8] = itemstack;
		} else if (this.slots[8].isItemEqual(itemstack)) {
			this.slots[8].stackSize = this.slots[8].stackSize + 4;
		}

		for (int i = 0; i < 8; i++) {
			this.slots[i].stackSize--;
			if (this.slots[i].stackSize <= 0) {
				this.slots[i] = null;
			}
		}

	}


	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			this.cookTime = nbt.getShort("cookTime");
			this.active = nbt.getShort("active");
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			nbt.setShort("cookTime", (short) this.cookTime);
			nbt.setShort("active", (short) this.active);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (0 < slot && slot < 8) {
			if (stack.getItem() == Calculator.circuitBoard) {
				return true;
			}
		} else if (slot == 0) {
			if (stack.getMaxStackSize() >= 4) {
				return true;
			}
		} else if (slot == 9 && DischargeValues.getValueOf(stack) > 0) {
			return true;
		}
		return false;

	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int par) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return slot == 0 ? output : slot == 1 ? input : slot == 2 ? circuits : slot == 3 ? circuits : slot == 4 ? circuits : slot == 5 ? circuits : input;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack p_102008_2_, int side) {
		if (slot == 8) {
			return true;
		}
		return false;
	}

	public boolean receiveClientEvent(int action, int param) {
		if (action == 1) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return true;
	}
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip){
		super.getWailaInfo(currenttip);
		if (cookTime > 0) {
			String active = FontHelper.translate("locator.state") + ":" + FontHelper.translate("locator.active");
			currenttip.add(active);
		} else {
			String idle = FontHelper.translate("locator.state") + ":" + FontHelper.translate("locator.idle");
			currenttip.add(idle);
		}
		return currenttip;		
	}

	@Override
	public int getCurrentProcessTime() {
		return cookTime;
	}

	@Override
	public int getProcessTime() {
		return furnaceSpeed;
	}

	@Override
	public double getEnergyUsage() {
		return (requiredEnergy * 1.0) / getProcessTime();
	}

	@Override
	public int getBaseProcessTime() {
		return furnaceSpeed;
	}
}
