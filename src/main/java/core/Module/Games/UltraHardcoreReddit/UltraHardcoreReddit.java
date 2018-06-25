package network.reborn.core.Module.Games.UltraHardcoreReddit;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import net.dean.jraw.http.oauth.OAuthException;
import network.reborn.core.API.Module;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.Games.*;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameData;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameDataMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.InventoryClickListener;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.PlayerStats;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Commands.HealthCommand;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Commands.StatsCommand;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Commands.UHCMenuCommand;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Database.RedditDatabase;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners.*;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.*;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Options.GoldenHeads;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.Post;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.RedditEngine;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.TwitterEngine;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UBL.LoadRemoteUBL;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UBL.PlayerJoinAsyncProcessing;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.github.paperspigot.Title;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UltraHardcoreReddit extends Game {
    //public static DatabaseManager db = null;
    public static String loadedPercent = "";
    public static String postURL = "";
    public static Scoreboard gameBoard;
    public static HashMap<UUID, Integer> kills = new HashMap<>();
    public static Integer pveKills = 0;
    public static int inc = 0;
    static int startTaskId = 0;
    static Boolean allowDamage = true;
    static int changeDamageTask = 0;
    private static World gameWorld;
    private static boolean deathmatch = false;
    int taskm = 0;
    private long startTime;
    private Map lobby;
    private long mapSize = 1500;
    private boolean isTeams = true;
    private LoadRemoteUBL ubl;

    public UltraHardcoreReddit(RebornCore core) {
        super(core, "Reddit UHC", "uhc-reddit", true, 1, new GameSettings(), Module.UHC_REDDIT);
        super.setGameState(GameState.STARTING);
        //setDescription("Ultra Hardcore is a Last Man Standing game where players (or teams) compete to survive both the harsh environment and the other players. Health regeneration is disabled, and some crafting recipes have been modified. This information is available in /uhc");
        super.setGameTemplate(GameTemplate.LAST_MAN_STANDING);
        super.getGameSettings().setGameLobby(new Location(Bukkit.getWorlds().get(0), -76, 100, -20));
        super.getGameSettings().setDefaultGameMode(GameMode.SURVIVAL);
        super.getGameSettings().setBuild(true);
        super.getGameSettings().setDestroy(true);
        super.getGameSettings().setPvp(false);
        super.getGameSettings().setPve(true);
        super.getGameSettings().setHunger(true);
        super.getGameSettings().setBuckets(true);
        super.getGameSettings().setDropItems(true);
        super.getGameSettings().setPickupItems(true);
        super.getGameSettings().setDropItemsOnDeath(true);
        super.getGameSettings().setDisableWeather(true);
        super.getGameSettings().setTeams(true);
        super.getGameSettings().setTeamSize(1);
        super.getGameSettings().setHealthInTab(true);

        lobby = new Map("UHC Lobby", "uhc", MapType.LOBBY);

        // Attempt to load the UHC lobby world
        if (!lobby.loadMap()) {
            System.out.println("Failed to load UHC Lobby! Shutting down!");
            Bukkit.shutdown();
            return;
        }

        if (lobby.getMapConfig().getConfiguration().isSet("Spawn")) {
            String spawnStr = lobby.getMapConfig().getConfiguration().getString("Spawn");
            String[] spawnSplit = spawnStr.split("\\|");
            double x = new Double(spawnSplit[0]);
            double y = new Double(spawnSplit[1]);
            double z = new Double(spawnSplit[2]);
            Location spawn = new Location(lobby.getWorld(), x, y, z);
            getGameSettings().setGameLobby(spawn);
        }

        //Registering the Ocean/Jungle Removal Tool before world creation
        OceanRemovalSystem ors = new OceanRemovalSystem();
        ors.replaceChunks();
        Bukkit.getPluginManager().registerEvents(ors, RebornCore.getRebornCore());

        // Here we'll generate a new world for the UHC game!
        File f = new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "generation.complete");
        File d = new File(Bukkit.getWorldContainer() + File.separator + "mapgen.bypass");
        if (!f.exists() && !d.exists()) {
            OtherUtil.deleteWorld("game");
        }
        gameWorld = Bukkit.createWorld(new WorldCreator("game"));
        Location spawn = new Location(gameWorld, 0.0, 1.0 * gameWorld.getHighestBlockYAt(new Location(gameWorld, 0.0, 10.0, 0.0)), 0.0);
        gameWorld.getWorldBorder().setCenter(gameWorld.getSpawnLocation().getBlockX(), gameWorld.getSpawnLocation().getBlockZ());
        gameWorld.getWorldBorder().setSize(mapSize * 2);
        gameWorld.getWorldBorder().setDamageBuffer(0);
        //Ensure 1.8 compatibility
        try {
            gameWorld.setGameRuleValue("spectatorsGenerateChunks", "false");
        } catch (Exception e) {

        }
        gameWorld.setGameRuleValue("naturalRegeneration", "false");
        if (!f.exists() && !d.exists()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb game set " + mapSize + " " + mapSize + " " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb game fill 100 0 true");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
        }
        if (!f.exists() && !d.exists()) {
            setGameState(GameState.STARTING);
        } else {
            setGameState(GameState.WAITING);
        }
        setMinPlayers(2);
        setMaxPlayers(80);
        // db = new DatabaseManager();
        // db.onEnable();
        Bukkit.getLogger().info("UHC-Reddit // Game loaded. Pausing timer start to load additional modules.");
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinMusicHandler(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new GoldenHeads(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathKickHandler(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new WLManager(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new GameMenu(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new GameManager(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new ChatListener(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new Post(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinStatsHandler(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new BlockBreakNotifyHandler(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new UHCManager(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new UHCMenu(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinAsyncProcessing(), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new StaffJoinCheck(), RebornCore.getRebornCore());
        injectRecipe();
        GameManager.initSettings();
        TwitterEngine.initializeTwitterFactory();
        try {
            RedditEngine.initializeRedditClient();
        } catch (OAuthException e) {
            ErrorDump ed = new ErrorDump(RedditEngine.class.getName(), e);
            ed.createSpigot();
        }
        //OH HOLY FUCK JESUS THE TEST ACTUALLY WOKED
        //RedditEngine.test();
        //Bukkit.getLogger().warning("SHORT URL:" + URLShortener.shorten("https://reborn.network"));

        AbstractCommand gdm = new GameDataMenu("archive", "/archive", "View game archive", new ArrayList<String>());
        gdm.register();
        AbstractCommand statcmd = new StatsCommand("stats");
        statcmd.register();
        AbstractCommand uhc = new UHCMenuCommand("uhc");
        uhc.register();
        AbstractCommand health = new HealthCommand("health");
        health.register();


        Logger.getLogger("Minecraft")
                .addHandler(new ConsoleLoggerHandler(RebornCore.getRebornCore()));
        for (Objective o : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            o.unregister();
        }
        Bukkit.getLogger().info("UHC-Reddit // Events registered and commands activated. Game ready.");
        lobby.getWorld().setDifficulty(Difficulty.PEACEFUL);
        try {
            RebornCore.getCoveAPI().getMySQLManager().getConnection().createStatement().execute("UPDATE Servers set MaxPlayers = '80' where ID = '" + RebornCore.getRebornCore().getConfig().getInt("Server ID") + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective o = board.registerNewObjective("lob", "dummy");
        o.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + getGameTitle());
        Score fil = o.getScore(ChatColor.AQUA + "");
        fil.setScore(15);
        Score host = o.getScore(ChatColor.GOLD + "Host:");
        host.setScore(14);
        Score hostName = o.getScore(ChatColor.YELLOW + GameManager.currentHost);
        hostName.setScore(13);
        Score fil2 = o.getScore(ChatColor.BLACK + "");
        fil2.setScore(12);
        Score slotText = o.getScore(ChatColor.GOLD + "Slots:");
        slotText.setScore(11);
        Score slots = o.getScore(ChatColor.YELLOW + String.valueOf(GameManager.slots));
        slots.setScore(10);
        Score fil4 = o.getScore(ChatColor.DARK_RED + "");
        fil4.setScore(9);
        Score teamText = o.getScore(ChatColor.GOLD + "Teams:");
        teamText.setScore(8);
        String teamSize = "No";
        if (getTeamSize() > 1) {
            teamSize = String.valueOf(getTeamSize());
        }
        Score teams = o.getScore(ChatColor.YELLOW + teamSize);
        teams.setScore(7);
        Score fil3 = o.getScore(ChatColor.BLUE + "");
        fil3.setScore(6);
        Score lobb = o.getScore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Lobby Mode");
        lobb.setScore(5);
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        //Call startTimer() when the Host triggers it via Game Menu.
        //startTimer();
        Bukkit.getLogger().info("UBL // Now attempting to load AutoUBL...");
        ubl = new LoadRemoteUBL(RebornCore.getRebornCore());
        ubl.download();
    }

    public static void broadcastUHCMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "UHC " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    public static void sendUHCMessage(String message, Player player) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "UHC " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    public static void injectRecipe() {
        ItemStack i = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Head");
        i.setItemMeta(im);
        ShapedRecipe head = new ShapedRecipe(i);
        head.shape("GGG", "GAG", "GGG");
        head.setIngredient('G', Material.GOLD_INGOT);
        head.setIngredient('A', Material.SKULL_ITEM, 3);
        Bukkit.addRecipe(head);


    }

    public static void setValue(String message) {
        if (message.contains("[Fill]")) {
            if (message.contains("~") && message.contains("%")) {
                int tild = message.indexOf('~');
                String c1 = Character.toString(message.charAt(tild + 1));
                String c2 = Character.toString(message.charAt(tild + 2));
                String c3 = Character.toString(message.charAt(tild + 3));
                String c4 = Character.toString(message.charAt(tild + 4));
                String c5 = Character.toString(message.charAt(tild + 5));
                if (c2.equals(".")) {
                    loadedPercent = (c1 + c2 + c3) + "%";
                } else if (c1.equals("1") && c2.equals("0") && c3.equals("0")) {
                    loadedPercent = (c1 + c2 + c3 + c4 + c5) + "%";
                } else {
                    loadedPercent = (c1 + c2 + c3 + c4) + "%";
                }
            }
        }
    }

    public static String milliToStandard(Long gt) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(gt),
                TimeUnit.MILLISECONDS.toMinutes(gt) - TimeUnit.HOURS
                        .toMinutes(TimeUnit.MILLISECONDS.toHours(gt)),
                TimeUnit.MILLISECONDS.toSeconds(gt) - TimeUnit.MINUTES
                        .toSeconds(TimeUnit.MILLISECONDS.toMinutes(gt)));
    }

    public static void disconnectUBL(UUID u) {
        Player p = Bukkit.getPlayer(u);
        Bukkit.getPlayer(u).kickPlayer(ChatColor.DARK_RED + "Kicked from Server" + "\n" + ChatColor.YELLOW + "IGN: " + ChatColor.RED + p.getName() + "\n" + ChatColor.YELLOW + "Reason: " + ChatColor.RED + "On Reddit UBL" + "\n" + ChatColor.YELLOW + "Staff: " + ChatColor.RED + "Reborn Network" + "\n" + ChatColor.YELLOW + "Notes: " + ChatColor.RED + "This applies to Reddit " + "\n" + ChatColor.RED + "UHC servers only");
        broadcastUHCMessage(ChatColor.YELLOW + p.getName() + ChatColor.RED + " was kicked for being on the UBL.");
    }

    public void setPostURL(String s) {
        postURL = s;
    }

    public Boolean isDeathmatch() {
        return deathmatch;
    }

    public void executeDeathmatch() {
        allowDamage = false;
        deathmatch = true;
        freezeAllPlayers();
        for (Player d : Bukkit.getOnlinePlayers()) {
          //ScatterEngine.freezeForMC18(d);
            d.playSound(d.getLocation(), Sound.ENDERMAN_SCREAM, 10, 0);
            d.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 10), true);
            d.sendTitle(new Title(ChatColor.RED + "MEETUP", ChatColor.YELLOW + "Time to fight!", 10, 40, 10));
        }
        //if (isTeams())
        ScatterEngine.startScatter(RebornCore.getCoveAPI().getGame(), gameWorld, 100.0, 20, ScatterType.MEETUP);
        startTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(), () -> {
            if (ScatterEngine.isScattered) {
                continueDMScatter();
                unfreezeAllPlayers();
                ScatterEngine.isScattered = false;
                allowDamage = false;
                for (GamePlayer p : getAlivePlayers()) {
                    ScatterEngine.unfreeze(p.getPlayer());
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                }
                UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GOLD + "You have invincibility for 10 seconds.");
                changeDamageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getCore(), () -> {
                    if (inc >= 10) {
                        allowDamage = true;
                        UltraHardcoreReddit.broadcastUHCMessage(ChatColor.RED + "You are no longer invincible.");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLAZE_BREATH, 10, 0);
                        }
                        Bukkit.getScheduler().cancelTask(changeDamageTask);
                    }
                    inc++;
                }, 0L, 20L);
                Bukkit.getScheduler().cancelTask(startTaskId);
            }
        }, 0L, 20L);
        /*for (Player d : Bukkit.getOnlinePlayers()) {
            unfreezeAllPlayers();
            d.removePotionEffect(PotionEffectType.BLINDNESS);
            allowDamage = true;
        }*/
    }

    public void continueDMScatter() {
        gameWorld.getWorldBorder().setCenter(gameWorld.getSpawnLocation());
        gameWorld.getWorldBorder().setSize(240);
        gameWorld.getWorldBorder().setSize(50, 600);
        gameWorld.getWorldBorder().setDamageBuffer(0);
        gameBoard.resetScores(ChatColor.YELLOW + "1500x1500");
        gameBoard.getObjective("game").getScore(ChatColor.YELLOW + "120x120").setScore(10);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getLocation().getWorld().getName().equals(lobby.getWorld().getName()))
            event.setCancelled(true); // Don't spawn entities in lobby
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (getGameState().equals(GameState.WAITING) && event.getTo().getBlockY() < 22)
            event.getPlayer().teleport(getGameSettings().getGameLobby()); //Prevent falling into void
    }

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        super.onPlayerDamage(event);
        if (!allowDamage) {
            event.setCancelled(true);
            return;
        }
        if (getGameState() != GameState.INGAME)
            return;
        if (getGameTime() < 30) { // No damage if the game has been going for less than 10 seconds! (Fall damage protection and just other things that could happen)
            event.setCancelled(true);
        }
    }

    public long getGameTime() {
        long current = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return current - startTime;
    }

    public void startGame() {
        for (SongPlayer sp : PlayerJoinMusicHandler.players.values()) {
            sp.destroy();
        }
        //freezeAllPlayers();
        for (Player p : Bukkit.getOnlinePlayers()) {
            //ScatterEngine.freezeForMC18(p);
        }
        allowDamage = false;
        getGameSettings().setDisableWeather(true);
        super.startGame();

        // Remove all entities that we don't want
        try {
            gameWorld.getEntities().stream().filter(entity -> entity.getType() != EntityType.ARMOR_STAND && !entity.isDead()).forEach(Entity::remove);
            gameWorld.setStorm(false);
            gameWorld.setThundering(false);
            gameWorld.setTime(0);
        } catch (Exception e) {
            Bukkit.getLogger().info("Exception - ignoring.");
        }

        /*for (final GamePlayer gamePlayer : getAlivePlayers()) {
            if (!gamePlayer.isOnline())
                return;
            Location location = gameWorld.getSpawnLocation();
            gamePlayer.getPlayer().teleport(location);
        }*/

        ScatterEngine.startScatter(this, gameWorld, 1500, 100, ScatterType.GAME);
        startTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(), () -> {
            if (ScatterEngine.isScattered) {
                continueGameStartA();
                ScatterEngine.isScattered = false;
                Bukkit.getScheduler().cancelTask(startTaskId);
            }
        }, 0L, 20L);

    }

    public void continueGameStartA() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("Welcome to UHC.");
        messages.add("In UHC, there are some game changes.");
        messages.add("Let's go over them while we start.");
        messages.add("1) Health regen is " + ChatColor.DARK_RED + "disabled");
        messages.add("2) Some crafting recipes are " + ChatColor.GRAY + "more difficult");
        messages.add("3) World Difficulty is set to " + ChatColor.DARK_GRAY + "Hard");
        messages.add("4) Players drop Skulls when killed");
        messages.add("5) These Skulls can craft " + ChatColor.GOLD + "Golden Heads");
        messages.add("Some other changes may be enabled.");
        messages.add("Please do /uhc to view them.");
        messages.add("Have fun, and good luck ;)");
        final int[] msg = {0};
        final int[] val = {10};
        taskm = Bukkit.getScheduler().scheduleSyncRepeatingTask(getCore(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(new Title(ChatColor.RED + "UHC", ChatColor.YELLOW + messages.get(msg[0]), 20, 60, 20));
                RebornCore.getCoveAPI().getGamePlayer(p).sendActionBar(ChatColor.GREEN + "Game starting in " + 5 * val[0] + " seconds", 60);
            }
            msg[0] += 1;
            val[0] -= 1;
            if (msg[0] == 11) {
                Bukkit.getScheduler().cancelTask(taskm);
                continueGameStartB();
            }
        }, 0L, 100L);
    }

    /*@Override
    public Scoreboard getSoloScoreboard(Player player, boolean showKills, boolean showDeaths, boolean showPlayers) {
        return super.getSoloScoreboard(player, showKills, false, showPlayers);
    }*/

    public void continueGameStartB() {
        startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        postURL = "";
        unfreezeAllPlayers();
        getGameSettings().setFrozen(false);
        Bukkit.getLogger().info("Unfroze players");
        UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GOLD + "You have invincibility for 30 seconds.");
        Bukkit.getLogger().info("Gave invincibility");
        File f = new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "generation.complete");
        if (f.exists()) {
            f.delete();
            Bukkit.getLogger().info("[OPT] Deleted generation.complete");
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Bukkit.getLogger().info("Entering loop");
            p.sendTitle(new Title(ChatColor.RED + "UHC", ChatColor.GREEN + "Game Start!", 20, 20, 20));
            Bukkit.getLogger().info("Sent title");
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 0);
            Bukkit.getLogger().info("Played sound");
            ScatterEngine.unfreeze(p);
            Bukkit.getLogger().info("Unfroze using scatterengine");
            p.setHealth(20.0);
            Bukkit.getLogger().info("Set Health");
            p.setSaturation((float) 40);
            Bukkit.getLogger().info("Set saturation");
            p.removePotionEffect(PotionEffectType.JUMP);
            p.setWalkSpeed(0.2f);
        }
        ScatterEngine.resetToDefault();
        //gameWorld.getEntities().stream().filter(entity -> entity.getType() != EntityType.ARMOR_STAND && !entity.isDead()).forEach(Entity::remove);
        Bukkit.getLogger().info("Filtered entities");
        changeDamageTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(getCore(), () -> {
            Bukkit.getLogger().info("ClearInvLoop fire");
            if (getGameTime() >= 30) {
                Bukkit.getLogger().info("Removing invincibility");
                allowDamage = true;
                UltraHardcoreReddit.broadcastUHCMessage(ChatColor.RED + "You are no longer invincible.");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLAZE_BREATH, 10, 0);
                }
                Bukkit.getScheduler().cancelTask(changeDamageTask);
            }
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(getCore(), () -> {
            if (getGameState() != GameState.INGAME)
                return;
            boolean pvpChange = false;
            //TODO: PVP TIME
            int pvpTime = 900;
            //int pvpTime = 60;
            if (getGameTime() == pvpTime && !getGameSettings().isPvp()) {
                getGameSettings().setPvp(true);
                pvpChange = true;
            }
            //TODO: MEETUP TIME
            long remainingTime = 3600 - getGameTime();
            //long remainingTime = 120 - getGameTime();
            long timeTillPVP = pvpTime - getGameTime();

            if (remainingTime <= 0 && !deathmatch) {
                executeDeathmatch();
                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (20) + " " + (100) + " true @a ");
                /*else
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (20) + " " + (100) + " false @a ");*/

            } else if (remainingTime < 0 && deathmatch) {
                return;
            }
            for (GamePlayer gamePlayer : getAlivePlayers()) {
                if (!gamePlayer.isOnline())
                    continue;
                if (getGameTime() >= pvpTime && getGameTime() <= (pvpTime + 5)) { // Show PVP has been enabled message for 4 seconds
                    if (pvpChange)
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 1F);
                    gamePlayer.sendActionBar(ChatColor.DARK_RED + "!!! " + ChatColor.RED + "" + ChatColor.BOLD + "PVP has been enabled" + ChatColor.DARK_RED + " !!!");
                } else if (getGameTime() < pvpTime) {
                    gamePlayer.sendActionBar(ChatColor.RED + "Meetup: " + OtherUtil.getDurationString(remainingTime, true) + " PVP: " + OtherUtil.getDurationString(timeTillPVP, true));
                } else {
                    gamePlayer.sendActionBar(ChatColor.RED + "Meetup: " + OtherUtil.getDurationString(remainingTime, true));
                }
            }
        }, 10L, 10L);
        gameBoard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective o = gameBoard.registerNewObjective("Health", "health");
        o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        Objective b = gameBoard.registerNewObjective("game", "dummy");
        b.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + getGameTitle());
        b.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score d0 = b.getScore(ChatColor.GRAY + "");
        d0.setScore(15);
        Score host = b.getScore(ChatColor.GOLD + "Host:");
        host.setScore(14);
        Score hostName = b.getScore(ChatColor.YELLOW + GameManager.currentHost);
        hostName.setScore(13);
        Score d1 = b.getScore(ChatColor.RED + "");
        d1.setScore(12);
        Score border = b.getScore(ChatColor.GOLD + "Border:");
        border.setScore(11);
        Score borderText = b.getScore(ChatColor.YELLOW + "1500x1500");
        borderText.setScore(10);
        Score d2 = b.getScore(ChatColor.GREEN + "");
        d2.setScore(9);
        Score lobb = b.getScore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Game Mode");
        lobb.setScore(8);

        Objective kills = gameBoard.registerNewObjective("kill", "dummy");
        kills.setDisplayName("" + ChatColor.RED + "" + ChatColor.BOLD + "Kills");
        Score e = kills.getScore(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PvE");
        e.setScore(1);
        e.setScore(0);

        for (Player a : Bukkit.getOnlinePlayers()) {
            a.setScoreboard(gameBoard);
            a.setHealth(19.0);
            a.setHealth(19.9);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(), () -> {
            if (gameBoard.getObjective(DisplaySlot.SIDEBAR).getDisplayName().contains(getGameTitle())) {
                gameBoard.getObjective("kill").setDisplaySlot(DisplaySlot.SIDEBAR);
            } else {
                gameBoard.getObjective("game").setDisplaySlot(DisplaySlot.SIDEBAR);
            }
        }, 600L, 600L);

        int id = RedditEngine.db.getLastUsedId() + 1;

        StringBuilder scenarios = new StringBuilder(10);
        for (String name : ScenariosAPI.scenarios.keySet()) {
            scenarios.append(name);
        }

        GameData data = new GameData(id, Bukkit.getOfflinePlayer(GameManager.currentHost).getUniqueId().toString(), String.valueOf(System.currentTimeMillis()), scenarios.toString(), UltraHardcoreReddit.postURL);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (getGameState().equals(GameState.INGAME)) {
            Player p = event.getEntity();
            Bukkit.getScheduler().scheduleAsyncDelayedTask(RebornCore.getRebornCore(), () -> {
                PlayerStats currentStats = RedditEngine.db.getStats(p.getUniqueId().toString());
                currentStats.setLosses(currentStats.getLosses() + 1);
                currentStats.setKills(kills.get(p.getUniqueId()));
                PlayerStats.queueStat(currentStats);
            });
            Objective o = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("kill");
            if (o != null) {
                Entity k = p.getKiller();
                if (k != null && k instanceof Player) {
                    PlayerStats newStats = new PlayerStats(p.getUniqueId().toString(), 0, 1, o.getScore(p.getName()).getScore());
                    String name = k.getName();
                    o.getScore(name).setScore(o.getScore(name).getScore() + 1);
                } else {
                    int i = o.getScore(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PvE").getScore();
                    pveKills++;
                    o.getScore(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PvE").setScore(i + 1);
                }
            }

        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //db.onDisable();
        // Delete game world
        File f = new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "generation.complete");
        File d = new File(Bukkit.getWorldContainer() + File.separator + "mapgen.bypass");
        if (!d.exists()) {
            if (!f.exists()) {
                OtherUtil.deleteWorld("game");
                try {
                    FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + "game"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onFillComplete(WorldBorderFillFinishedEvent event) {
        setGameState(GameState.WAITING);
        File f = new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "generation.complete");
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RedditDatabase getDBManager() {
        return RedditEngine.db;
    }

}
