package network.reborn.core.Module.Games.TacticalAssault;

import network.reborn.core.API.Module;
import network.reborn.core.Module.Games.*;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TacticalAssault extends Game {
    public static Map selectedMap;
    public static HashMap<Integer, String> teams = new HashMap<>();
    public static HashMap<UUID, Boolean> players = new HashMap<>();
    public static HashMap<Integer, Boolean> bannerDestroyed = new HashMap<>();
    public static HashMap<Integer, Location> bannerLocations = new HashMap<>();
    public static HashMap<Integer, Location> respawnLocations = new HashMap<>();
    public static List<Location> dispensers = new ArrayList<>();
    public static List<Location> midDispensers = new ArrayList<>();
    public static HashMap<Location, Integer> dispenserSelections = new HashMap<>();
    public static HashMap<Location, Integer> dispenserIronLevels = new HashMap<>();
    public static HashMap<Location, Integer> dispenserGoldLevels = new HashMap<>();
    public static HashMap<Location, Integer> dispenserDiamLevels = new HashMap<>();
    public static HashMap<UUID, Location> openInventory = new HashMap<>();
    public static Shop shop;
    public static ArrayList<Location> blocksPlaced = new ArrayList<>();
    public static HashMap<Integer, Integer> ticks = new HashMap<>();
    private ArrayList<Map> maps = new ArrayList<>();

    public TacticalAssault(RebornCore core) {
        super(core, "Tactical Assault", "tacticalassault", false, 2, new GameSettings(), Module.TACTICALASSAULT);
        getGameSettings().setGameLobby(new Location(Bukkit.getWorlds().get(0), -76, 5, -20));
        getGameSettings().setDefaultGameMode(GameMode.SURVIVAL);
        getGameSettings().setBuild(true);
        getGameSettings().setDestroy(true);
        getGameSettings().setHunger(true);
        getGameSettings().setBuckets(true);
        getGameSettings().setDropItems(true);
        getGameSettings().setPickupItems(true);
        getGameSettings().setDamage(false);
        getGameSettings().setDropItemsOnDeath(false);
        getGameSettings().setAutoPopulateChests(false);
        freezeAllPlayers();

        Bukkit.getPluginManager().registerEvents(new Listeners(), RebornCore.getRebornCore());
        setupShop();
        Bukkit.getPluginManager().registerEvents(shop, RebornCore.getRebornCore());

        maps.add(new Map("Forest", "forest", MapType.GAME));

        if (maps.isEmpty())
            return;

        selectedMap = maps.get(OtherUtil.randInt(0, maps.size() - 1));
        mapStuff();
        selectedMap.resetMapOptions();

        setMinPlayers(4);
        setMaxPlayers(selectedMap.getSpawnCages().size());

        setupGameVariables();

        Bukkit.getScheduler().runTaskLater(getCore(), () -> {
            setMaxPlayers(selectedMap.getSpawnCages().size());
        }, 20L);
        startTimer();
        startDispenserLoops();
    }

    public static int getTeam(Player player) {
        for (java.util.Map.Entry<Integer, String> e : teams.entrySet()) {
            if (e.getValue().equals(player.getName())) {
                return e.getKey();
            }
        }
        return 5; // Dafuq
    }

    public static void checkLastPlayer() {
        int alive = 0;
        ArrayList<UUID> aliveUUID = new ArrayList<>();
        for (java.util.Map.Entry<UUID, Boolean> entry : players.entrySet()) {
            if (entry.getValue()) {
                alive++;
                aliveUUID.add(entry.getKey());
            }
        }
        if (alive == 1) {
            Player winner = Bukkit.getPlayer(aliveUUID.get(0));
            RebornCore.getCoveAPI().getGame().endGame(winner.getName());
        } else if (alive == 0) {
            RebornCore.getCoveAPI().getGame().endGame("");
        }
    }

    public static int getLevelFromSelectedItem(int item, Location location) {
        if (item == 0) {
            return dispenserIronLevels.get(location);
        } else if (item == 1) {
            return dispenserGoldLevels.get(location);
        } else if (item == 2) {
            return dispenserDiamLevels.get(location);
        } else {
            return 0;
        }
    }

    private void startDispenserLoops() {
        int i = 0;
        for (Location location : dispensers) {
            ticks.put(i, 0);
            handleDispenser(location, i);
            i++;
        }
    }

    private void handleDispenser(Location location, int id) {
        Bukkit.getScheduler().runTaskTimer(getCore(), new Runnable() {
            public void run() {
                ticks.put(id, ticks.get(id) + 1);
                if (getGameState().equals(GameState.INGAME)) {
                    int selectedItem = 0;
                    selectedItem = dispenserSelections.get(location);
                    int level = 0;
                    if (selectedItem == 0) {
                        // Iron
                        level = TacticalAssault.dispenserIronLevels.get(location);
                    } else if (selectedItem == 1) {
                        // Gold
                        level = TacticalAssault.dispenserIronLevels.get(location);
                    } else if (selectedItem == 2) {
                        level = TacticalAssault.dispenserIronLevels.get(location);
                    }

                    if (level == 0) {
                        return;
                    }

                    //System.out.println("Ticks: " + ticks.get(id));
                    //System.out.println("Level: " + level);
                    //System.out.println("ID: " + id);

                    if (level == 1) {
                        if (ticks.get(id) < 20) return;
                    } else if (level == 2) {
                        if (ticks.get(id) < 15) return;
                    } else if (level == 3) {
                        if (ticks.get(id) < 10) return;
                    } else if (level == 4) {
                        if (ticks.get(id) < 5) return;
                    } else {
                        //DAFUQ??
                    }

                    ticks.put(id, 0);

                    int count = 0;
                    List<Entity> entityList = location.getWorld().getEntities().stream().filter(e -> location.distance(e.getLocation()) <= 2).collect(Collectors.toList());
                    for (Entity entity : entityList) {
                        if (entity instanceof Item) {
                            count += ((Item) entity).getItemStack().getAmount();
                        }
                    }

                    if (count > (10 * (3 - level))) return;

                    if (!location.getChunk().isLoaded()) return;
                    if (selectedItem == 0) {
                        Item item = location.getWorld().dropItem(location.clone().add(0.5, 1, 0.5), new ItemStack(Material.IRON_INGOT));
                        item.setVelocity(new Vector(0, .05, 0));
                    }
                }
            }
        }, 1L, 1L);
    }

    public List<Entity> getNearbyEntities(Location location, int size, int dispenserID) {
        List<Entity> entityList = location.getWorld().getEntities().stream().filter(e -> location.distance(e.getLocation()) <= size).collect(Collectors.toList());
//		System.out.println(dispenserID + ": " + entityList.size());
        return entityList;
    }

    public void setupShop() {
        shop = new Shop();
        //FOOD
        ShopItem steak = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.COOKED_BEEF, 1), new ItemStack(Material.IRON_INGOT), 1);
        shop.addShopItem(steak);
        ShopItem goldApple = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GOLDEN_APPLE, 1), new ItemStack(Material.GOLD_INGOT), 2);
        shop.addShopItem(goldApple);
        ShopItem godApple = new ShopItem(Shop.ShopItemType.FOOD, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(godApple);
        //BLOCKS
        ShopItem stone = new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.STONE, 3), new ItemStack(Material.IRON_INGOT), 1);
        shop.addShopItem(stone);
        ShopItem glass = new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.GLASS, 3), new ItemStack(Material.IRON_INGOT), 1);
        shop.addShopItem(glass);
        ShopItem obsidian = new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.GOLD_INGOT), 3);
        shop.addShopItem(obsidian);
        ShopItem hayblock = new ShopItem(Shop.ShopItemType.BLOCK, new ItemStack(Material.HAY_BLOCK), new ItemStack(Material.IRON_INGOT), 3);
        shop.addShopItem(hayblock);
        //TOOLS
        ItemStack stonepickaxeItem = new ItemStack(Material.STONE_PICKAXE);
        stonepickaxeItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        ShopItem stonepickaxe = new ShopItem(Shop.ShopItemType.TOOLS, stonepickaxeItem, new ItemStack(Material.IRON_INGOT), 32);
        shop.addShopItem(stonepickaxe);
        ItemStack ironpickaxeItem = new ItemStack(Material.IRON_PICKAXE);
        ironpickaxeItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        ShopItem ironpickaxe = new ShopItem(Shop.ShopItemType.TOOLS, ironpickaxeItem, new ItemStack(Material.IRON_INGOT), 64);
        shop.addShopItem(ironpickaxe);
        ItemStack diamondpickaxeItem = new ItemStack(Material.DIAMOND_PICKAXE);
        diamondpickaxeItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        ShopItem diamondpickaxe = new ShopItem(Shop.ShopItemType.TOOLS, diamondpickaxeItem, new ItemStack(Material.GOLD_INGOT), 32);
        shop.addShopItem(diamondpickaxe);
        ItemStack opdiamondpickaxeItem = new ItemStack(Material.DIAMOND_PICKAXE);
        opdiamondpickaxeItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 8);
        ShopItem opdiamondpickaxe = new ShopItem(Shop.ShopItemType.TOOLS, opdiamondpickaxeItem, new ItemStack(Material.DIAMOND), 32);
        shop.addShopItem(opdiamondpickaxe);
        //ARMOR
        ItemStack leatherHelmetItem = new ItemStack(Material.LEATHER_HELMET);
        leatherHelmetItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem leatherHelmet = new ShopItem(Shop.ShopItemType.ARMOR, leatherHelmetItem, new ItemStack(Material.IRON_INGOT), 5);
        shop.addShopItem(leatherHelmet);
        ItemStack leatherChestplateItem = new ItemStack(Material.LEATHER_CHESTPLATE);
        leatherChestplateItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem leatherChestplate = new ShopItem(Shop.ShopItemType.ARMOR, leatherChestplateItem, new ItemStack(Material.IRON_INGOT), 5);
        shop.addShopItem(leatherChestplate);
        ItemStack leatherLeggingsItem = new ItemStack(Material.LEATHER_LEGGINGS);
        leatherLeggingsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem leatherLeggings = new ShopItem(Shop.ShopItemType.ARMOR, leatherLeggingsItem, new ItemStack(Material.IRON_INGOT), 5);
        shop.addShopItem(leatherLeggings);
        ItemStack leatherBootsItem = new ItemStack(Material.LEATHER_BOOTS);
        leatherBootsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem leatherBoots = new ShopItem(Shop.ShopItemType.ARMOR, leatherBootsItem, new ItemStack(Material.IRON_INGOT), 5);
        shop.addShopItem(leatherBoots);
        ItemStack ironHelmetItem = new ItemStack(Material.IRON_HELMET);
        ironHelmetItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem ironHelmet = new ShopItem(Shop.ShopItemType.ARMOR, ironHelmetItem, new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(ironHelmet);
        ItemStack ironChestplateItem = new ItemStack(Material.IRON_CHESTPLATE);
        ironChestplateItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem ironChestplate = new ShopItem(Shop.ShopItemType.ARMOR, ironChestplateItem, new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(ironChestplate);
        ItemStack ironLeggingsItem = new ItemStack(Material.IRON_LEGGINGS);
        ironLeggingsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem ironLeggings = new ShopItem(Shop.ShopItemType.ARMOR, ironLeggingsItem, new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(ironLeggings);
        ItemStack ironBootsItem = new ItemStack(Material.IRON_BOOTS);
        ironBootsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        ShopItem ironBoots = new ShopItem(Shop.ShopItemType.ARMOR, ironBootsItem, new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(ironBoots);
        ItemStack diamondHelmetItem = new ItemStack(Material.DIAMOND_HELMET);
        diamondHelmetItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ShopItem diamondHelmet = new ShopItem(Shop.ShopItemType.ARMOR, diamondHelmetItem, new ItemStack(Material.DIAMOND), 32);
        shop.addShopItem(diamondHelmet);
        ItemStack diamondChestplateItem = new ItemStack(Material.DIAMOND_CHESTPLATE);
        diamondChestplateItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ShopItem diamondChestplate = new ShopItem(Shop.ShopItemType.ARMOR, diamondChestplateItem, new ItemStack(Material.DIAMOND), 32);
        shop.addShopItem(diamondChestplate);
        ItemStack diamondLeggingsItem = new ItemStack(Material.DIAMOND_LEGGINGS);
        diamondLeggingsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ShopItem diamondLeggings = new ShopItem(Shop.ShopItemType.ARMOR, diamondLeggingsItem, new ItemStack(Material.DIAMOND), 32);
        shop.addShopItem(diamondLeggings);
        ItemStack diamondBootsItem = new ItemStack(Material.DIAMOND_BOOTS);
        diamondBootsItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ShopItem diamondBoots = new ShopItem(Shop.ShopItemType.ARMOR, diamondBootsItem, new ItemStack(Material.DIAMOND), 32);
        shop.addShopItem(diamondBoots);
        //WEAPONS
        ItemStack woodSwordItem = new ItemStack(Material.WOOD_SWORD);
        woodSwordItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        ShopItem woodSword = new ShopItem(Shop.ShopItemType.WEAPON, woodSwordItem, new ItemStack(Material.IRON_INGOT), 10);
        shop.addShopItem(woodSword);
        ShopItem woodAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.WOOD_AXE), new ItemStack(Material.IRON_INGOT), 15);
        shop.addShopItem(woodAxe);
        ItemStack stoneSwordItem = new ItemStack(Material.STONE_SWORD);
        stoneSwordItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        ShopItem stoneSword = new ShopItem(Shop.ShopItemType.WEAPON, stoneSwordItem, new ItemStack(Material.IRON_INGOT), 32);
        shop.addShopItem(stoneSword);
        ShopItem stoneAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.STONE_AXE), new ItemStack(Material.IRON_INGOT), 45);
        shop.addShopItem(stoneAxe);
        ItemStack ironSwordItem = new ItemStack(Material.IRON_SWORD);
        ironSwordItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        ShopItem ironSword = new ShopItem(Shop.ShopItemType.WEAPON, ironSwordItem, new ItemStack(Material.GOLD_INGOT), 32);
        shop.addShopItem(ironSword);
        ShopItem ironAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.IRON_AXE), new ItemStack(Material.GOLD_INGOT), 45);
        shop.addShopItem(ironAxe);
        ItemStack diamondSwordItem = new ItemStack(Material.DIAMOND_SWORD);
        diamondSwordItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        ShopItem diamondSword = new ShopItem(Shop.ShopItemType.WEAPON, diamondSwordItem, new ItemStack(Material.DIAMOND), 10);
        shop.addShopItem(diamondSword);
        ShopItem diamondAxe = new ShopItem(Shop.ShopItemType.WEAPON, new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND), 15);
        shop.addShopItem(diamondAxe);
        //REDSTONE
        ShopItem tnt = new ShopItem(Shop.ShopItemType.REDSTONE, new ItemStack(Material.TNT, 1), new ItemStack(Material.GOLD_INGOT), 10);
        shop.addShopItem(tnt);
        // I don't know and I don't care.

        shop.setupShopCategoryGUI(Shop.ShopItemType.FOOD);
        shop.setupShopCategoryGUI(Shop.ShopItemType.BLOCK);
        shop.setupShopCategoryGUI(Shop.ShopItemType.TOOLS);
        shop.setupShopCategoryGUI(Shop.ShopItemType.ARMOR);
        shop.setupShopCategoryGUI(Shop.ShopItemType.WEAPON);
        shop.setupShopCategoryGUI(Shop.ShopItemType.REDSTONE);
    }

    public void setupGameVariables() {
        if (!selectedMap.getMapConfig().getConfiguration().isSet("teamvariables")) {
            //Map is not made for TacticalAssault
            return;
        }
        List<String> teamVariables = selectedMap.getMapConfig().getConfiguration().getStringList("teamvariables");
        int i = 0;
        for (String teamVariable : teamVariables) {
            // Team Format is bannerx,bannery,bannerz|respawnx,respawny,respawnz,respawnyaw
            String[] variableSplit = teamVariable.split("\\|");
            if (variableSplit.length < 2) {
                //Map is not properly made.
                getCore().getLogger().warning("The map " + selectedMap.getName() + " was loaded but the Team Variables are improperly configured.");
                //TODO: Handle invalid team configurations
                return;
            }
            String[] bannerPosition = variableSplit[0].split(",");
            String[] respawnPosition = variableSplit[1].split(",");
            bannerLocations.put(i, new Location(selectedMap.getWorld(), Integer.valueOf(bannerPosition[0]), Integer.valueOf(bannerPosition[1]), Integer.valueOf(bannerPosition[2])));
            respawnLocations.put(i, new Location(selectedMap.getWorld(), Integer.valueOf(respawnPosition[0]) + .5, Integer.valueOf(respawnPosition[1]) + .5, Integer.valueOf(respawnPosition[2]) + .5, Float.valueOf(respawnPosition[3]), 0F));
            i++;
        }
        if (!selectedMap.getMapConfig().getConfiguration().isSet("dispensers")) {
            //Map is not made for TacticalAssault
            return;
        }
        List<String> dispensers = selectedMap.getMapConfig().getConfiguration().getStringList("dispensers");
        for (String dispenserVar : dispensers) {
            // DISPENSER FORMAT IS dx,dy,dz|startinglevel
            String[] variableSplit = dispenserVar.split("\\|");
            if (variableSplit.length != 2) {
                //Map is not properly made.
                getCore().getLogger().warning("The map " + selectedMap.getName() + " was loaded but the dispensers are improperly configured.");
                //TODO: Handle invalid dispenser configurations
                return;
            }
            String[] location = variableSplit[0].split(",");
            int x = Integer.valueOf(location[0]);
            int y = Integer.valueOf(location[1]);
            int z = Integer.valueOf(location[2]);
            int startingLevel = Integer.valueOf(variableSplit[1]);
            Location location1 = new Location(selectedMap.getWorld(), x, y, z);
            TacticalAssault.dispensers.add(location1);
            TacticalAssault.dispenserIronLevels.put(location1, 1);
            TacticalAssault.dispenserGoldLevels.put(location1, 0);
            TacticalAssault.dispenserDiamLevels.put(location1, 0);
            dispenserSelections.put(location1, 0);
            i++;
        }
    }

    @Override
    public Scoreboard getSoloScoreboard(Player player, boolean showKills, boolean showDeaths, boolean showPlayers) {
        return super.getSoloScoreboard(player, showKills, false, showPlayers);
    }

    public void mapStuff() {
        if (selectedMap != null && selectedMap.loadMap()) {
            selectedMap.loadSpawnCages();
            Bukkit.broadcastMessage(ChatColor.GREEN + "Loaded Map: " + ChatColor.GOLD + selectedMap.getMapConfig().getDisplayName() + ChatColor.GREEN + " - Version " + ChatColor.GOLD + selectedMap.getMapConfig().getVersion() + ChatColor.GREEN + " - Created by " + ChatColor.GOLD + selectedMap.getMapConfig().getAuthor());
            System.out.println(ChatColor.GREEN + "Loaded Map: " + ChatColor.GOLD + selectedMap.getMapConfig().getDisplayName() + ChatColor.GREEN + " - Version " + ChatColor.GOLD + selectedMap.getMapConfig().getVersion() + ChatColor.GREEN + " - Created by " + ChatColor.GOLD + selectedMap.getMapConfig().getAuthor());
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to load map! Server shutting down!");
            System.out.println(ChatColor.RED + "Failed to load map! Server shutting down!");
            Bukkit.shutdown();
        }
        selectedMap.getWorld().setGameRuleValue("doMobSpawning", "false");
        selectedMap.getWorld().setGameRuleValue("doTileDrops", "false");
        Bukkit.getScheduler().runTaskLater(getCore(), () -> selectedMap.tellDatabase(), 20L);
    }

    @Override
    public void startGame() {
        super.startGame();
        unfreezeAllPlayers();
        selectedMap.releaseCages();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, (float) 0.5, 1);
        }
        Bukkit.getScheduler().runTaskLater(getCore(), () -> getGameSettings().setDamage(true), 40L);
        //TODO: Decide that to do with this finally.
        bannerDestroyed.put(0, false);
        bannerDestroyed.put(1, false);
        bannerDestroyed.put(2, false);
        bannerDestroyed.put(3, false);
    }

    @Override
    public List<String> getExtraMessages() {
        List<String> messages = new ArrayList<>();
        messages.add(ChatColor.GOLD + "Total Coins Earned: %coins%");
        messages.add(ChatColor.GOLD + " ");
        messages.add(ChatColor.GREEN + "Your Kills: %kills%");
        return messages;
    }
}
