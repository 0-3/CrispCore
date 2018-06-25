package network.reborn.core.Module.SMP;

import network.reborn.core.Module.Module;
import network.reborn.core.Module.SMP.Commands.*;
import network.reborn.core.Module.SMP.Handlers.Shop;
import network.reborn.core.Module.SMP.Handlers.ShopItem;
import network.reborn.core.Module.SMP.Listeners.*;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SMP extends Module {
    public static Location spawn = new Location(Bukkit.getWorld("world"), 0.5, 75, 0.5, -180, 0);
    public static List<UUID> teleportList = new ArrayList<>();
    public static HashMap<UUID, UUID> teleportRequests = new HashMap<>(); // Used pre-accept/reject
    public static HashMap<UUID, UUID> pendingTeleports = new HashMap<>(); // Used post-accept
    public static Shop shop;

    public SMP(RebornCore rebornCore) {
        super("SMP", "smp", rebornCore, network.reborn.core.API.Module.SMP);
        setupShop();
        registerListeners();
        registerCommands();
    }

    public void setupShop() {
        shop = new Shop();

        // Food Items

        ShopItem apple8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.APPLE, 8), 50);
        shop.addShopItem(apple8);

        ShopItem apple16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.APPLE, 16), 100);
        shop.addShopItem(apple16);

        ShopItem apple32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.APPLE, 32), 200);
        shop.addShopItem(apple32);

        ShopItem apple64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.APPLE, 64), 400);
        shop.addShopItem(apple64);

        ShopItem rawBeef8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_BEEF, 8), 50);
        shop.addShopItem(rawBeef8);

        ShopItem rawBeef16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_BEEF, 16), 100);
        shop.addShopItem(rawBeef16);

        ShopItem rawBeef32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_BEEF, 32), 200);
        shop.addShopItem(rawBeef32);

        ShopItem rawBeef64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_BEEF, 64), 400);
        shop.addShopItem(rawBeef64);

        ShopItem rawPork8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.PORK, 8), 50);
        shop.addShopItem(rawPork8);

        ShopItem rawPork16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.PORK, 16), 100);
        shop.addShopItem(rawPork16);

        ShopItem rawPork32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.PORK, 32), 200);
        shop.addShopItem(rawPork32);

        ShopItem rawPork64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.PORK, 64), 400);
        shop.addShopItem(rawPork64);

        ShopItem rawChicken8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_CHICKEN, 8), 50);
        shop.addShopItem(rawChicken8);

        ShopItem rawChicken16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_CHICKEN, 16), 100);
        shop.addShopItem(rawChicken16);

        ShopItem rawChicken32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_CHICKEN, 32), 200);
        shop.addShopItem(rawChicken32);

        ShopItem rawChicken64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.RAW_CHICKEN, 64), 400);
        shop.addShopItem(rawChicken64);

        ShopItem cookedBeef8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_BEEF, 8), 100);
        shop.addShopItem(cookedBeef8);

        ShopItem cookedBeef16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_BEEF, 16), 200);
        shop.addShopItem(cookedBeef16);

        ShopItem cookedBeef32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_BEEF, 32), 400);
        shop.addShopItem(cookedBeef32);

        ShopItem cookedBeef64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_BEEF, 64), 800);
        shop.addShopItem(cookedBeef64);

        ShopItem cookedPork8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GRILLED_PORK, 8), 100);
        shop.addShopItem(cookedPork8);

        ShopItem cookedPork16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GRILLED_PORK, 16), 200);
        shop.addShopItem(cookedPork16);

        ShopItem cookedPork32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GRILLED_PORK, 32), 400);
        shop.addShopItem(cookedPork32);

        ShopItem cookedPork64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GRILLED_PORK, 64), 800);
        shop.addShopItem(cookedPork64);

        ShopItem cookedChicken8 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_CHICKEN, 8), 100);
        shop.addShopItem(cookedChicken8);

        ShopItem cookedChicken16 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_CHICKEN, 16), 200);
        shop.addShopItem(cookedChicken16);

        ShopItem cookedChicken32 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_CHICKEN, 32), 400);
        shop.addShopItem(cookedChicken32);

        ShopItem cookedChicken64 = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_CHICKEN, 64), 800);
        shop.addShopItem(cookedChicken64);

        // Armor Items

        ShopItem leatherHelmet = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.LEATHER_HELMET), 30);
        shop.addShopItem(leatherHelmet);

        ShopItem leatherChestplate = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.LEATHER_CHESTPLATE), 48);
        shop.addShopItem(leatherChestplate);

        ShopItem leatherLeggings = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.LEATHER_LEGGINGS), 42);
        shop.addShopItem(leatherLeggings);

        ShopItem leatherBoots = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.LEATHER_BOOTS), 24);
        shop.addShopItem(leatherBoots);

        ShopItem ironHelmet = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.IRON_HELMET), 200);
        shop.addShopItem(ironHelmet);

        ShopItem ironChestplate = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.IRON_CHESTPLATE), 320);
        shop.addShopItem(ironChestplate);

        ShopItem ironLeggings = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.IRON_LEGGINGS), 280);
        shop.addShopItem(ironLeggings);

        ShopItem ironBoots = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.IRON_BOOTS), 160);
        shop.addShopItem(ironBoots);

        ShopItem diamondHelmet = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.DIAMOND_HELMET), 500);
        shop.addShopItem(diamondHelmet);

        ShopItem diamondChestplate = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.DIAMOND_CHESTPLATE), 800);
        shop.addShopItem(diamondChestplate);

        ShopItem diamondLeggings = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.DIAMOND_LEGGINGS), 700);
        shop.addShopItem(diamondLeggings);

        ShopItem diamondBoots = new ShopItem(Shop.ShopItemType.ARMOR, new ItemStack(Material.DIAMOND_BOOTS), 400);
        shop.addShopItem(diamondBoots);

        // Weapon/Tools

        ShopItem woodSword = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.WOOD_SWORD), 16);
        shop.addShopItem(woodSword);

        ShopItem woodPickaxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.WOOD_PICKAXE), 28);
        shop.addShopItem(woodPickaxe);

        ShopItem woodAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.WOOD_AXE), 28);
        shop.addShopItem(woodAxe);

        ShopItem woodShovel = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.WOOD_SPADE), 20);
        shop.addShopItem(woodShovel);

        ShopItem stoneSword = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.STONE_SWORD), 20);
        shop.addShopItem(stoneSword);

        ShopItem stonePickaxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.STONE_PICKAXE), 34);
        shop.addShopItem(stonePickaxe);

        ShopItem stoneAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.STONE_AXE), 34);
        shop.addShopItem(stoneAxe);

        ShopItem stoneShovel = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.STONE_SPADE), 30);
        shop.addShopItem(stoneShovel);

        ShopItem ironSword = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.IRON_SWORD), 88);
        shop.addShopItem(ironSword);

        ShopItem ironPickaxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.IRON_PICKAXE), 136);
        shop.addShopItem(ironPickaxe);

        ShopItem ironAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.IRON_AXE), 136);
        shop.addShopItem(ironAxe);

        ShopItem ironShovel = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.IRON_SPADE), 56);
        shop.addShopItem(ironShovel);

        ShopItem diamondSword = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.DIAMOND_SWORD), 208);
        shop.addShopItem(diamondSword);

        ShopItem diamondPickaxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.DIAMOND_PICKAXE), 316);
        shop.addShopItem(diamondPickaxe);

        ShopItem diamondAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.DIAMOND_AXE), 316);
        shop.addShopItem(diamondAxe);

        ShopItem diamondShovel = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.DIAMOND_SPADE), 116);
        shop.addShopItem(diamondShovel);

        // Blocks

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.STONE, 16), 48));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.STONE, 64), 192));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GRASS, 16), 48));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GRASS, 64), 192));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.DIRT, 16), 32));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.DIRT, 64), 128));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.COBBLESTONE, 16), 32));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.COBBLESTONE, 64), 128));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.WOOD, 16), 64));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.WOOD, 64), 256));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.LOG, 16), 256));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.LOG, 64), 1024));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.SAND, 16), 32));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.SAND, 64), 128));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GRAVEL, 16), 32));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GRAVEL, 64), 128));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.SANDSTONE, 16), 128));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.SANDSTONE, 64), 512));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GLASS, 16), 240));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GLASS, 64), 960));

        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.LEAVES, 16), 32));
        shop.addShopItem(new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.LEAVES, 64), 128));


        // Setup inventories/GUI's
        shop.setupShopCategoryGUI(Shop.ShopItemType.FOOD);
        shop.setupShopCategoryGUI(Shop.ShopItemType.ARMOR);
        shop.setupShopCategoryGUI(Shop.ShopItemType.WEAPON);
        shop.setupShopCategoryGUI(Shop.ShopItemType.BLOCK);
    }

    private void registerListeners() {
        rebornCore.getServer().getPluginManager().registerEvents(new RealPlayerMove(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new Roulette(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new PlayerJoin(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new HelpCommand(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(shop, rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new Respawn(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new PlayerQuit(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new PlayerDeath(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new PlayerDamage(), rebornCore);
        rebornCore.getServer().getPluginManager().registerEvents(new AdminEvents(), rebornCore);
    }

    private void registerCommands() {
        rebornCore.getCommand("spawn").setExecutor(new SpawnCommand());
        rebornCore.getCommand("balance").setExecutor(new BalanceCommand());
        rebornCore.getCommand("home").setExecutor(new HomeCommand());
        rebornCore.getCommand("sethome").setExecutor(new SetHomeCommand());
        rebornCore.getCommand("delhome").setExecutor(new DelHomeCommand());
        rebornCore.getCommand("shop").setExecutor(new ShopCommand());
        rebornCore.getCommand("rtp").setExecutor(new RTPCommand());
        rebornCore.getCommand("tpa").setExecutor(new TeleportCommand());
        rebornCore.getCommand("tpaccept").setExecutor(new TeleportCommand());
        rebornCore.getCommand("tpdeny").setExecutor(new TeleportCommand());
        rebornCore.getCommand("admin").setExecutor(new AdminCommand());
    }

}
