package sonar.calculator.mod;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sonar.calculator.mod.common.item.calculators.CalculatorItem;
import sonar.calculator.mod.common.item.calculators.CraftingCalc;
import sonar.calculator.mod.common.item.calculators.FlawlessCalc;
import sonar.calculator.mod.common.item.calculators.InfoCalc;
import sonar.calculator.mod.common.item.calculators.ScientificCalc;
import sonar.calculator.mod.common.item.misc.CalculatorScreen;
import sonar.calculator.mod.common.item.misc.CircuitBoard;
import sonar.calculator.mod.common.item.misc.EndDiamond;
import sonar.calculator.mod.common.item.misc.Grenade;
import sonar.calculator.mod.common.item.misc.SmallStone;
import sonar.calculator.mod.common.item.misc.Soil;
import sonar.calculator.mod.common.item.misc.UpgradeCircuit;
import sonar.calculator.mod.common.item.modules.AdvancedTerrainModule;
import sonar.calculator.mod.common.item.modules.EnergyModule;
import sonar.calculator.mod.common.item.modules.HealthModule;
import sonar.calculator.mod.common.item.modules.HungerModule;
import sonar.calculator.mod.common.item.modules.LocatorModule;
import sonar.calculator.mod.common.item.modules.NutritionModule;
import sonar.calculator.mod.common.item.modules.StorageModule;
import sonar.calculator.mod.common.item.modules.TerrainModule;
import sonar.calculator.mod.common.item.tools.CalcAxe;
import sonar.calculator.mod.common.item.tools.CalcHoe;
import sonar.calculator.mod.common.item.tools.CalcPickaxe;
import sonar.calculator.mod.common.item.tools.CalcShovel;
import sonar.calculator.mod.common.item.tools.CalcSword;
import sonar.core.common.item.SonarMetaItem;
import sonar.core.common.item.SonarSeeds;
import sonar.core.common.item.SonarSeedsFood;

public class CalculatorItems extends Calculator {

	public static ArrayList<Item> registeredItems = new ArrayList();

	public static final Item.ToolMaterial ReinforcedStone = EnumHelper.addToolMaterial("ReinforcedStone", 1, 250, 5.0F, 1.5F, 5);
	public static final Item.ToolMaterial RedstoneMaterial = EnumHelper.addToolMaterial("RedstoneMaterial", 2, 800, 7.5F, 2.5F, 18);
	public static final Item.ToolMaterial EnrichedGold = EnumHelper.addToolMaterial("EnrichedGold", 3, 1000, 8.0F, 0.0F, 20);
	public static final Item.ToolMaterial ReinforcedIron = EnumHelper.addToolMaterial("ReinforcedIron", 2, 400, 7.0F, 2.0F, 10);
	public static final Item.ToolMaterial WeakenedDiamond = EnumHelper.addToolMaterial("WeakenedDiamond", 3, 1400, 8.0F, 3.0F, 10);
	public static final Item.ToolMaterial FlawlessDiamond = EnumHelper.addToolMaterial("FlawlessDiamond", 3, 1800, 14.0F, 5.0F, 30);
	public static final Item.ToolMaterial FireDiamond = EnumHelper.addToolMaterial("FireDiamond", 3, 2600, 16.0F, 7.0F, 30);
	public static final Item.ToolMaterial ElectricDiamond = EnumHelper.addToolMaterial("ElectricDiamond", 4, 10000, 18.0F, 10.0F, 30);
	public static final Item.ToolMaterial EndForged = EnumHelper.addToolMaterial("EndForged", 6, -1, 50F, 16.0F, 30);

	public static Item registerItem(String name, Item item) {
		item.setCreativeTab(Calculator);
		GameRegistry.registerItem(item.setUnlocalizedName(name), name);
		registeredItems.add(item);
		return item;
	}

	public static void registerItems() {

		// calculators
		itemInfoCalculator = registerItem("InfoCalculator", new InfoCalc());
		itemCalculator = registerItem("Calculator", new CalculatorItem());
		itemCraftingCalculator = registerItem("CraftingCalculator", new CraftingCalc());
		itemScientificCalculator = registerItem("ScientificCalculator", new ScientificCalc());
		itemFlawlessCalculator = registerItem("FlawlessCalculator", new FlawlessCalc());

		// modules
		itemStorageModule = registerItem("StorageModule", new StorageModule());
		itemHungerModule = registerItem("HungerModule", new HungerModule());
		itemHealthModule = registerItem("HealthModule", new HealthModule());
		itemNutritionModule = registerItem("NutritionModule", new NutritionModule());
		itemTerrainModule = registerItem("TerrainModule", new TerrainModule());
		itemAdvancedTerrainModule = registerItem("AdvancedTerrainModule", new AdvancedTerrainModule());
		itemEnergyModule = registerItem("EnergyModule", new EnergyModule());
		itemLocatorModule = registerItem("LocatorModule", new LocatorModule());

		// misc
		soil = registerItem("Soil", new Soil());
		small_stone = registerItem("SmallStone", new SmallStone());

		// upgrades
		speedUpgrade = registerItem("SpeedUpgrade", new UpgradeCircuit(0));
		energyUpgrade = registerItem("EnergyUpgrade", new UpgradeCircuit(1));
		voidUpgrade = registerItem("VoidUpgrade", new UpgradeCircuit(2));

		// calculator parts
		calculator_screen = registerItem("CalculatorScreen", new CalculatorScreen());
		
		calculator_assembly = registerItem("CalculatorAssembly", new Item());
		advanced_assembly = registerItem("AdvancedAssembly", new Item());
		atomic_module = registerItem("AtomicModule", new Item());
		atomic_assembly = registerItem("AtomicAssembly", new Item());
		atomic_binder = registerItem("AtomicBinder", new Item());

		// calculator parts
		// tools
		/*
		wrench = new Wrench().setUnlocalizedName("Wrench").setCreativeTab(Calculator).setMaxStackSize(1).setTextureName(modid + ":" + "wrench");
		GameRegistry.registerItem(wrench, "Wrench");
		sickle = new Sickle().setUnlocalizedName("Sickle").setCreativeTab(Calculator).setTextureName(modid + ":" + "sickle").setMaxStackSize(1);
		GameRegistry.registerItem(sickle, "Sickle");
		ObsidianKey = new ObsidianKey().setUnlocalizedName("ObsidianKey").setCreativeTab(Calculator).setTextureName(modid + ":" + "codedkey");
		GameRegistry.registerItem(ObsidianKey, "ObsidianKey");
		/
		 * */
		 
		//swords
		reinforced_sword = registerItem("ReinforcedSword", new CalcSword(ReinforcedStone));
		enrichedgold_sword = registerItem("EnrichedGoldSword", new CalcSword(EnrichedGold));
		reinforcediron_sword = registerItem("ReinforcedIronSword", new CalcSword(ReinforcedIron));
		redstone_sword = registerItem("RedstoneSword", new CalcSword(RedstoneMaterial));
		weakeneddiamond_sword = registerItem("WeakenedDiamondSword", new CalcSword(WeakenedDiamond));
		flawlessdiamond_sword = registerItem("FlawlessDiamondSword", new CalcSword(FlawlessDiamond));	
		firediamond_sword = registerItem("FireDiamondSword", new CalcSword(FireDiamond));		
		electric_sword = registerItem("ElectricSword", new CalcSword(ElectricDiamond));		
		endforged_sword = registerItem("EndForgedSword", new CalcSword(EndForged));

		//pickaxes
		reinforced_pickaxe = registerItem("ReinforcedPickaxe", new CalcPickaxe(ReinforcedStone));
		enrichedgold_pickaxe = registerItem("EnrichedGoldPickaxe", new CalcPickaxe(EnrichedGold));
		reinforcediron_pickaxe = registerItem("ReinforcedIronPickaxe", new CalcPickaxe(ReinforcedIron));
		redstone_pickaxe = registerItem("RedstonePickaxe", new CalcPickaxe(RedstoneMaterial));
		weakeneddiamond_pickaxe = registerItem("WeakenedDiamondPickaxe", new CalcPickaxe(WeakenedDiamond));
		flawlessdiamond_pickaxe = registerItem("FlawlessDiamondPickaxe", new CalcPickaxe(FlawlessDiamond));	
		firediamond_pickaxe = registerItem("FireDiamondPickaxe", new CalcPickaxe(FireDiamond));		
		electric_pickaxe = registerItem("ElectricPickaxe", new CalcPickaxe(ElectricDiamond));		
		endforged_pickaxe = registerItem("EndForgedPickaxe", new CalcPickaxe(EndForged));
		
		//axes
		reinforced_axe = registerItem("ReinforcedAxe", new CalcAxe(ReinforcedStone));
		enrichedgold_axe = registerItem("EnrichedGoldAxe", new CalcAxe(EnrichedGold));
		reinforcediron_axe = registerItem("ReinforcedIronAxe", new CalcAxe(ReinforcedIron));
		redstone_axe = registerItem("RedstoneAxe", new CalcAxe(RedstoneMaterial));
		weakeneddiamond_axe = registerItem("WeakenedDiamondAxe", new CalcAxe(WeakenedDiamond));
		flawlessdiamond_axe = registerItem("FlawlessDiamondAxe", new CalcAxe(FlawlessDiamond));	
		firediamond_axe = registerItem("FireDiamondAxe", new CalcAxe(FireDiamond));		
		electric_axe = registerItem("ElectricAxe", new CalcAxe(ElectricDiamond));		
		endforged_axe = registerItem("EndForgedAxe", new CalcAxe(EndForged));		
		
		//shovels
		reinforced_shovel = registerItem("ReinforcedShovel", new CalcShovel(ReinforcedStone));
		enrichedgold_shovel = registerItem("EnrichedGoldShovel", new CalcShovel(EnrichedGold));
		reinforcediron_shovel = registerItem("ReinforcedIronShovel", new CalcShovel(ReinforcedIron));
		redstone_shovel = registerItem("RedstoneShovel", new CalcShovel(RedstoneMaterial));
		weakeneddiamond_shovel = registerItem("WeakenedDiamondShovel", new CalcShovel(WeakenedDiamond));
		flawlessdiamond_shovel = registerItem("FlawlessDiamondShovel", new CalcShovel(FlawlessDiamond));	
		firediamond_shovel = registerItem("FireDiamondShovel", new CalcShovel(FireDiamond));		
		electric_shovel = registerItem("ElectricShovel", new CalcShovel(ElectricDiamond));		
		endforged_shovel = registerItem("EndForgedShovel", new CalcShovel(EndForged));

		//hoes
		reinforced_hoe = registerItem("ReinforcedHoe", new CalcHoe(ReinforcedStone));
		enrichedgold_hoe = registerItem("EnrichedGoldHoe", new CalcHoe(EnrichedGold));
		reinforcediron_hoe = registerItem("ReinforcedIronHoe", new CalcHoe(ReinforcedIron));
		redstone_hoe = registerItem("RedstoneHoe", new CalcHoe(RedstoneMaterial));
		weakeneddiamond_hoe = registerItem("WeakenedDiamondHoe", new CalcHoe(WeakenedDiamond));
		flawlessdiamond_hoe = registerItem("FlawlessDiamondHoe", new CalcHoe(FlawlessDiamond));	
		firediamond_hoe = registerItem("FireDiamondHoe", new CalcHoe(FireDiamond));		
		electric_hoe = registerItem("ElectricHoe", new CalcHoe(ElectricDiamond));		
		endforged_hoe = registerItem("EndForgedHoe", new CalcHoe(EndForged));
		
		// materials
		enrichedGold = registerItem("EnrichedGold", new Item());
		enrichedgold_ingot = registerItem("EnrichedGoldIngot", new Item());
		reinforcediron_ingot = registerItem("ReinforcedIronIngot", new Item());
		redstone_ingot = registerItem("RedstoneIngot", new Item());
		weakeneddiamond = registerItem("WeakenedDiamond", new Item());
		flawlessdiamond = registerItem("FlawlessDiamond", new Item());
		firediamond = registerItem("FireDiamond", new Item());
		electricDiamond = registerItem("ElectricDiamond", new Item());
		endDiamond = registerItem("EndDiamond", new EndDiamond());

		// gems
		large_amethyst = registerItem("LargeAmethyst", new Item());
		small_amethyst = registerItem("SmallAmethyst", new Item());
		shard_amethyst = registerItem("ShardAmethyst", new Item());
		large_tanzanite = registerItem("LargeTanzanite", new Item());
		small_tanzanite = registerItem("SmallTanzanite", new Item());
		shard_tanzanite = registerItem("ShardTanzanite", new Item());

		// crops
		// cropBroccoliPlant = new CalculatorCrops(0, 0).setBlockName("BroccoliPlant");
		// GameRegistry.registerBlock(cropBroccoliPlant, "BroccoliPlant");
		// /cropPrunaePlant = new CalculatorCrops(1, 2).setBlockName("PrunaePlant");
		// GameRegistry.registerBlock(cropPrunaePlant, "PrunaePlant");
		// cropFiddledewPlant = new CalculatorCrops(2, 3).setBlockName("FiddledewPlant");
		// GameRegistry.registerBlock(cropFiddledewPlant, "FiddledewPlant");

		// seeds
		broccoliSeeds = registerItem("BroccoliSeeds", new SonarSeeds(cropBroccoliPlant, Blocks.farmland, 0));
		prunaeSeeds = registerItem("PrunaeSeeds", new SonarSeeds(cropPrunaePlant, Blocks.farmland, 2));
		fiddledewFruit = registerItem("FiddledewFruit", new SonarSeedsFood(16, 0.6F, cropFiddledewPlant, Blocks.farmland, 3));

		// food
		broccoli = registerItem("Broccoli", new ItemFood(1, 0.2F, false));
		pear = registerItem("Pear", new ItemFood(12, 2.0F, false));
		rotten_pear = registerItem("RottenPear", new ItemFood(1, 0.1F, false));
		cookedBroccoli = registerItem("CookedBroccoli", new ItemFood(9, 0.6F, false));

		// fuels
		coal_dust = registerItem("CoalDust", new Item());
		enriched_coal = registerItem("EnrichedCoal", new Item());
		purified_coal = registerItem("PurifiedCoal", new Item());
		firecoal = registerItem("FireCoal", new Item());
		controlled_Fuel = registerItem("ControlledFuel", new Item());

		// grenades
		grenadecasing = registerItem("GrenadeCasing", new Item());
		baby_grenade = registerItem("BabyGrenade", new Grenade(0));
		grenade = registerItem("Grenade", new Grenade(1));
		circuitBoard = registerItem("CircuitBoard", new CircuitBoard().setHasSubtypes(true).setMaxStackSize(1));
		circuitDamaged = registerItem("CircuitDamaged", new SonarMetaItem(14).setHasSubtypes(true).setMaxStackSize(1));
		circuitDirty = registerItem("CircuitDirty", new SonarMetaItem(14).setHasSubtypes(true).setMaxStackSize(1));

		// circuitDamaged = new ItemCircuitDamaged().setHasSubtypes(true).setUnlocalizedName("CircuitDamaged").setCreativeTab(Calculator).setMaxStackSize(1);
		// GameRegistry.registerItem(circuitDamaged, "CircuitDamaged");
		// circuitDirty = new ItemCircuitDirty().setHasSubtypes(true).setUnlocalizedName("CircuitDirty").setCreativeTab(Calculator).setMaxStackSize(1);
		// GameRegistry.registerItem(circuitDirty, "CircuitDirty");
		/* // chest generation items ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(circuitBoard, 0, 1, 1, 2)); ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(circuitDirty, 0, 1, 1, 5)); ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(circuitDamaged, 0, 1, 1, 5)); ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(itemCalculator, 0, 1, 1, 4)); ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(itemCraftingCalculator, 0, 1, 1, 4)); ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(itemInfoCalculator, 0, 1, 1, 4)); ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(Item.getItemFromBlock(reinforcedStoneBlock), 0, 5, 20, 12)); ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(wrench, 0, 1, 1, 3)); ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(itemInfoCalculator, 0, 1, 1, 10)); */
	}
}
