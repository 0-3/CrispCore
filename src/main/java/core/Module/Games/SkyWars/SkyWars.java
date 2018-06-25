package network.reborn.core.Module.Games.SkyWars;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Games.*;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.ChestPopulator;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class SkyWars extends Game {
    public static Map selectedMap;
    private ArrayList<Map> maps = new ArrayList<>();
    private SkyWarsMode SWMode = SkyWarsMode.DEFAULT;

    public SkyWars(RebornCore core) {
        super(core, "SkyWars", "skywars", false, 2, new GameSettings(), network.reborn.core.API.Module.SKYWARS);
        getGameSettings().setGameLobby(new Location(Bukkit.getWorlds().get(0), -76, 5, -20));
        getGameSettings().setDefaultGameMode(GameMode.SURVIVAL);
        getGameSettings().setBuild(true);
        getGameSettings().setDestroy(true);
        getGameSettings().setHunger(true);
        getGameSettings().setBuckets(true);
        getGameSettings().setDropItems(true);
        getGameSettings().setPickupItems(true);
        getGameSettings().setDamage(false);
        getGameSettings().setDropItemsOnDeath(true);
        getGameSettings().setAutoPopulateChests(true);
        setGameTemplate(GameTemplate.LAST_MAN_STANDING);
        freezeAllPlayers();
        kitManager = new SkyWarsKitManager(this);


//        if (OtherUtil.randInt(0, 1) == 1)
//            gameType = SkyWarsMode.OP; // 20% chance of an OP game

//        JSONObject jsonObject = getCoveServer().getExtraData();
//        jsonObject.put("Mode", gameType.toString());
//        getCoveServer().setExtraData(jsonObject);

        Bukkit.getPluginManager().registerEvents(new Listeners(), RebornCore.getRebornCore());

        // Add maps
        maps.add(new Map("Little Wenham", "LittleWenham", MapType.GAME));
        maps.add(new Map("Flake Land", "FlakeLand", MapType.GAME));
        maps.add(new Map("Bikini Bottom", "BikiniBottom", MapType.GAME));

        if (maps.isEmpty())
            return; // No maps :(

        selectedMap = maps.get(OtherUtil.randInt(0, maps.size() - 1));
        mapStuff();
        selectedMap.resetMapOptions();

        doChestPopulating();

        setMinPlayers(3);
//        try {
////            setMinPlayers(spawnPoints.size() / 4);
//        } catch (Exception ignored) {
//
//        }
        setMaxPlayers(selectedMap.getSpawnCages().size());

        Bukkit.getScheduler().runTaskLater(getCore(), () -> {
            setMaxPlayers(selectedMap.getSpawnCages().size());
        }, 20L);
        startTimer();
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
        Bukkit.getScheduler().runTaskLater(getCore(), () -> selectedMap.tellDatabase(), 20L);
    }

    public void doChestPopulating() {
        if (SWMode == SkyWarsMode.OP) {
            ChestPopulator.addChestItem(new ItemStack(Material.STONE, 16), 80);
            ChestPopulator.addChestItem(new ItemStack(Material.STONE, 32), 60);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD, 16), 80);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD, 32), 60);
            ChestPopulator.addChestItem(new ItemStack(Material.SNOW_BALL, 16), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND_HELMET), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND_LEGGINGS), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND_BOOTS), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.SNOW_BALL, 64), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND_SWORD), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_SWORD), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.PORK, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.COOKED_BEEF, 3), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.COOKED_BEEF, 6), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.APPLE), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.BOW), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.ARROW, 8), 60);
            ChestPopulator.addChestItem(new ItemStack(Material.ARROW, 16), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.ARROW, 64), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.FISHING_ROD), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_HELMET), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_CHESTPLATE), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_LEGGINGS), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_BOOTS), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.STICK, 2), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_INGOT), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.TNT, 5), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.TNT, 10), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.FLINT_AND_STEEL), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.LAVA_BUCKET), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.WATER_BUCKET), 80);
        } else {
            ChestPopulator.addChestItem(new ItemStack(Material.STONE, 16), 80);
            ChestPopulator.addChestItem(new ItemStack(Material.STONE, 32), 60);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD, 16), 80);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD, 32), 60);
            ChestPopulator.addChestItem(new ItemStack(Material.SNOW_BALL, 8), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.SNOW_BALL, 64), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_BEEF, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.PORK, 3), 25);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_CHICKEN, 3), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.ROTTEN_FLESH, 3), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.COOKED_BEEF, 3), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.COOKED_BEEF, 6), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.STONE_SWORD), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD_SWORD), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.STONE_AXE), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.APPLE), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.WOOD_AXE), 40);
            ChestPopulator.addChestItem(new ItemStack(Material.BOW), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.ARROW, 8), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.ARROW, 16), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.FISHING_ROD), 15);
            ChestPopulator.addChestItem(new ItemStack(Material.LEATHER_HELMET), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.LEATHER_CHESTPLATE), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.LEATHER_LEGGINGS), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.LEATHER_BOOTS), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.CHAINMAIL_HELMET), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.RAW_FISH, 3), 50);
            ChestPopulator.addChestItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.CHAINMAIL_BOOTS), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.GOLD_HELMET), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.GOLD_CHESTPLATE), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.GOLD_LEGGINGS), 5);
            ChestPopulator.addChestItem(new ItemStack(Material.GOLD_BOOTS), 10);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_HELMET), 1);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_CHESTPLATE), 1);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_LEGGINGS), 1);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_BOOTS), 1);
            ChestPopulator.addChestItem(new ItemStack(Material.STICK, 2), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.DIAMOND), 1);
            ChestPopulator.addChestItem(new ItemStack(Material.IRON_INGOT), 2);
            ChestPopulator.addChestItem(new ItemStack(Material.TNT, 5), 4);
            ChestPopulator.addChestItem(new ItemStack(Material.TNT, 10), 2);
            ChestPopulator.addChestItem(new ItemStack(Material.FLINT_AND_STEEL), 20);
            ChestPopulator.addChestItem(new ItemStack(Material.LAVA_BUCKET), 30);
            ChestPopulator.addChestItem(new ItemStack(Material.WATER_BUCKET), 40);
        }
    }


    @Override
    public void startGame() {
        super.startGame();
        unfreezeAllPlayers();
        selectedMap.releaseCages();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, (float) 0.5, 1);
            if (SWMode == SkyWarsMode.OP) {
                RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
                rebornPlayer.sendTitle(ChatColor.RED + "OP Mode", "", 20, 40, 20);
            }
        }
        Bukkit.getScheduler().runTaskLater(getCore(), () -> getGameSettings().setDamage(true), 40L);
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
