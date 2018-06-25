package network.reborn.core;

import me.bigteddy98.bannerboard.api.BannerBoardManager;
import me.bigteddy98.bannerboard.api.PlaceHolder;
import network.reborn.core.API.*;
import network.reborn.core.Commands.*;
import network.reborn.core.Handlers.AchievementHandler;
import network.reborn.core.Listeners.*;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameDataMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Commands.ScenarioTestCommand;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.ScenariosMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Pregame.Parkour.ParkourSystem;
import network.reborn.core.Module.Games.UltraHardcoreReddit.ScenariosAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOptionsAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.Util.AbstractCommand;
import network.reborn.core.Util.Database.MySQLManager;
import network.reborn.core.Util.Lag;
import network.reborn.core.Util.ScatterEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RebornCore extends JavaPlugin {
    public static HashMap<Integer, CoveServer> servers = new HashMap<>();
    public static AchievementHandler achievementHandler;
    private static RebornCore rebornCore;
    private static CoveAPI coveAPI;
    private boolean syncing = false;

    public static RebornCore getRebornCore() {
        return rebornCore;
    }

    public RebornCore() {
        //Just here so other plugins can (maybe) hook RebornCore :/
    }

    public static CoveAPI getCoveAPI() {
        return coveAPI;
    }

    public static ArrayList<CoveServer> getServers(network.reborn.core.API.Module module) {
        return getServers(module, null);
    }

    public static ArrayList<CoveServer> getServers(network.reborn.core.API.Module module, GameState gameState) {
        ArrayList<CoveServer> serverArray = new ArrayList<>();
        for (Map.Entry<Integer, CoveServer> entry : servers.entrySet()) {
            if (entry.getValue().getModule() != module || (gameState != null && entry.getValue().getGameState() != gameState) || !entry.getValue().isOnline())
                continue;
            serverArray.add(entry.getValue());
        }
        return serverArray;
    }

    public void onEnable() {
        saveDefaultConfig();
        saveConfig();
        rebornCore = this;
        coveAPI = new CoveAPI(this);
        coveAPI.loadModule();
        registerListeners();
        registerCommands();
//		new StopPushing(); // TODO Fix this
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 20L, 1L);
        PlayerMove.doAFKStuff();
        for (Player player : Bukkit.getOnlinePlayers()) {
            RebornPlayer rebornPlayer = getCoveAPI().getCovePlayer(player);
            rebornPlayer.setRunJoin(true);
        }

        bannerboardPlaceholders();

        achievementHandler = new AchievementHandler();

        // Sync servers
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::syncServers, 0L, 40L);

        try {
            if (getCoveAPI().getModule().getModule() == Module.HUB) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                    for (RebornPlayer rebornPlayer : getCoveAPI().getOnlineCovePlayers()) {
                        if (rebornPlayer.isVanished()) {
                            rebornPlayer.sendActionBar(ChatColor.WHITE + "You are currently " + ChatColor.RED + "VANISHED");
                        } else if (rebornPlayer.isNicked()) {
                            rebornPlayer.sendActionBar(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED");
                        }
                    }
                }, 20L, 40L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getCoveAPI().getModule().equals(Module.UHC_REDDIT) || getCoveAPI().getModule().equals(Module.ULTRA_HARDCORE)) {
            try {
                ScenariosAPI.generateAllInformation();
                UHCOptionsAPI.generateAllInformation();
                if (getCoveAPI().getModule().equals(Module.UHC_REDDIT)) {
                    UltraHardcoreReddit.injectRecipe();
                    ArrayList<String> v = new ArrayList<String>();
                    v.add("arch");
                    AbstractCommand a = new GameDataMenu("archive", "/archive", "Access Archive", v);
                    a.register();
                    getLogger().info("UHC has loaded required data.");
                }
                AbstractCommand stc = new ScenarioTestCommand("stc");
                stc.register();
                getLogger().info("SUCCESS! Reflections has loaded all UHC scenarios!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> getRebornAPI().getOnlineCovePlayers().stream().filter(RebornPlayer::isVanished).forEach(covePlayer -> covePlayer.sendActionBar(ChatColor.WHITE + "You are currently " + ChatColor.RED + "VANISHED")), 20L, 40L);
    }

    private void bannerboardPlaceholders() {
        PlaceHolder gold = new PlaceHolder(this) {
            @Override
            public String onReplace(Player player) {
                RebornPlayer rebornPlayer = getCoveAPI().getCovePlayer(player);
                return String.valueOf(rebornPlayer.getBalance("Gold"));
            }
        };
        BannerBoardManager.getAPI().registerPlaceHolder("gold", gold);

        PlaceHolder serverRank = new PlaceHolder(this) {
            @Override
            public String onReplace(Player player) {
                RebornPlayer rebornPlayer = getCoveAPI().getCovePlayer(player);
                return rebornPlayer.getServerRank().getNiceName(false);
            }
        };
        BannerBoardManager.getAPI().registerPlaceHolder("serverrank", serverRank);

        PlaceHolder donorRank = new PlaceHolder(this) {
            @Override
            public String onReplace(Player player) {
                RebornPlayer rebornPlayer = getCoveAPI().getCovePlayer(player);
                return rebornPlayer.getServerRank().getNiceName(false);
            }
        };
        BannerBoardManager.getAPI().registerPlaceHolder("donorrank", donorRank);

        PlaceHolder rank = new PlaceHolder(this) {
            @Override
            public String onReplace(Player player) {
                RebornPlayer rebornPlayer = getCoveAPI().getCovePlayer(player);
                if (rebornPlayer.getServerRank() != ServerRank.DEFAULT)
                    return rebornPlayer.getServerRank().getNiceName(false);
                else if (rebornPlayer.getDonorRank() != DonorRank.DEFAULT)
                    return rebornPlayer.getDonorRank().getNiceName(false);
                else
                    return "";
            }
        };
        BannerBoardManager.getAPI().registerPlaceHolder("rank", rank);
    }

    public void onDisable() {
        RebornCore.getCoveAPI().getModule().onDisable(); // Run onDisable
        RebornCore.getCoveAPI().getModule().getCoveServer().setOffline();
        try {
            if (RebornCore.getCoveAPI().getGame().getModule().equals(Module.UHC_REDDIT)) {
                if (((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).getDBManager() != null) {
                    ((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).getDBManager().onDisable();
                }
            }
        } catch (Exception e) {

        }
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new HologramListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new ScenariosMenu(), this);
        getServer().getPluginManager().registerEvents(new ParkourSystem(), this);
        getServer().getPluginManager().registerEvents(new ScatterEngine(), this);
    }

    private void registerCommands() {
        try {
            getServer().getPluginCommand("core").setExecutor(new CoreCommand());
            getServer().getPluginCommand("module").setExecutor(new ModuleCommand());
            getServer().getPluginCommand("rank").setExecutor(new RankCommand());
            getServer().getPluginCommand("help").setExecutor(new HelpCommand());
            getServer().getPluginCommand("disguise").setExecutor(new DisguiseCommand());
            getServer().getPluginCommand("undisguise").setExecutor(new UndisguiseCommand());
            OtherEssentialCommands commands = new OtherEssentialCommands();
            getServer().getPluginCommand("gmc").setExecutor(commands);
            getServer().getPluginCommand("gma").setExecutor(commands);
            getServer().getPluginCommand("gms").setExecutor(commands);
            getServer().getPluginCommand("gm").setExecutor(commands);
            getServer().getPluginCommand("gm0").setExecutor(commands);
            getServer().getPluginCommand("gm1").setExecutor(commands);
            getServer().getPluginCommand("gm2").setExecutor(commands);
            getServer().getPluginCommand("gm3").setExecutor(commands);
            getServer().getPluginCommand("tdf").setExecutor(commands);
            getServer().getPluginCommand("tppos").setExecutor(commands);
            getServer().getPluginCommand("feed").setExecutor(commands);
            getServer().getPluginCommand("heal").setExecutor(commands);
            getServer().getPluginCommand("speed").setExecutor(commands);
            getServer().getPluginCommand("ptime").setExecutor(commands);
            getServer().getPluginCommand("sm").setExecutor(new SMCommand());
            getServer().getPluginCommand("lag").setExecutor(new LagCommand());
            getServer().getPluginCommand("opme").setExecutor(new OPMeCommand());
            getServer().getPluginCommand("staff").setExecutor(new StaffCommand());
            getServer().getPluginCommand("fly").setExecutor(new FlyCommand());
            getServer().getPluginCommand("afk").setExecutor(new AfkCommand());
            getServer().getPluginCommand("currency").setExecutor(new CurrencyCommand());
            getServer().getPluginCommand("vanish").setExecutor(new VanishCommand());
            getServer().getPluginCommand("nick").setExecutor(new NickCommand());
            getServer().getPluginCommand("enderchest").setExecutor(new EnderChestCommand());
            getServer().getPluginCommand("nuke").setExecutor(new NukeCommand());
            getServer().getPluginCommand("burn").setExecutor(new BurnCommand());
            getServer().getPluginCommand("fireball").setExecutor(new FireballCommand());
            getServer().getPluginCommand("hat").setExecutor(new HatCommand());
            getServer().getPluginCommand("invsee").setExecutor(new InvseeCommand());
            getServer().getPluginCommand("unrank").setExecutor(new UnrankCommand());
            getServer().getPluginCommand("servergui").setExecutor(new ServerGUICommand());
            getServer().getPluginCommand("report").setExecutor(new ReportCommand());
            getServer().getPluginCommand("teleport").setExecutor(new TeleportCommand());
            getServer().getPluginCommand("give").setExecutor(new GiveCommand());
            getServer().getPluginCommand("slap").setExecutor(new SlapCommand());
            AbstractCommand nc = new NameColorCommand("namecolor");
            nc.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncServers() {
        if (syncing)
            return;
        syncing = true;
        boolean debug = false;
        MySQLManager manager = getCoveAPI().getMySQLManager();
        if (debug) System.out.println("Syncing servers...");
        String sql = "SELECT * FROM `Servers` WHERE `Online` = 1;";
        try {
            ResultSet resultSet = manager.getConnection().createStatement().executeQuery(sql);
            if (resultSet.next()) {
                if (debug) System.out.println("Found servers... Updating...");
                resultSet.beforeFirst();
                ArrayList<Integer> keep = new ArrayList<>();
                while (resultSet.next()) {
                    keep.add(resultSet.getInt("ID"));
                    String IP = resultSet.getString("IP");
                    if (IP == null || IP.equals("")) {
                        IP = "localhost";
                    }
                    network.reborn.core.API.Module module = network.reborn.core.API.Module.OTHER;
                    GameState gameState;
                    if (resultSet.getString("Name").toLowerCase().contains("hub"))
                        module = network.reborn.core.API.Module.HUB;
                    if (resultSet.getString("GameState") != null)
                        gameState = GameState.valueOf(resultSet.getString("GameState"));
                    else
                        gameState = GameState.STARTING;
                    if (resultSet.getString("Name").toLowerCase().contains("skywars")) {
                        module = network.reborn.core.API.Module.SKYWARS;
                        if (resultSet.getString("GameState") != null)
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        else
                            gameState = GameState.STARTING;
                    }
                    if (resultSet.getString("Name").toLowerCase().contains("ultra hardcore")) {
                        module = Module.ULTRA_HARDCORE;
                        if (resultSet.getString("GameState") != null)
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        else
                            gameState = GameState.STARTING;
                    }
                    if (resultSet.getString("Name").toLowerCase().contains("port protector")) {
                        module = network.reborn.core.API.Module.PORT_PROTECTOR;
                        if (resultSet.getString("GameState") != null)
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        else
                            gameState = GameState.STARTING;
                    }
                    if (resultSet.getString("Name").toLowerCase().contains("tactical assault")) {
                        module = network.reborn.core.API.Module.TACTICALASSAULT;
                        if (resultSet.getString("GameState") != null)
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        else
                            gameState = GameState.STARTING;
                    }
                    if (resultSet.getString("Name").toLowerCase().contains("reddit uhc")) {
                        module = network.reborn.core.API.Module.UHC_REDDIT;
                        if (resultSet.getString("GameState") != null)
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        else
                            gameState = GameState.STARTING;
                    }
                    if (resultSet.getString("Name").toLowerCase().contains("apionly")) {
                        module = network.reborn.core.API.Module.APIONLY;
                        if (resultSet.getString("GameState") != null) {
                            gameState = GameState.valueOf(resultSet.getString("GameState"));
                        } else {
                            gameState = GameState.STARTING;

                        }
                    }
                    CoveServer coveServer = new CoveServer(resultSet.getInt("ID"), resultSet.getString("Name"), IP, resultSet.getInt("Port"), module, gameState, resultSet.getInt("Players"), resultSet.getInt("MaxPlayers"));
                    if (resultSet.getString("Data") != null && !resultSet.getString("Data").isEmpty()) {
                        JSONParser parser = new JSONParser();
                        try {
                            coveServer.setExtraData((JSONObject) parser.parse(resultSet.getString("Data")), false);
                        } catch (ParseException ignored) {
                        }
                    }
//                    if (resultSet.getString("Name").toLowerCase().contains("hub"))
//                        coveServer.setModule(Module.HUB);
                    servers.put(coveServer.getID(), coveServer);
                    if (debug)
                        System.out.println("Added Server: " + resultSet.getInt("ID") + " (" + IP + ":" + resultSet.getInt("Port") + ")");
                }
                for (Map.Entry<Integer, CoveServer> entry : servers.entrySet()) {
                    if (!keep.contains(entry.getKey())) {
                        entry.getValue().setOffline();
                    } else {
                        entry.getValue().setOnline();
                    }
                }
            } else {
                if (debug) System.out.println("No online servers found...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        syncing = false;
    }

}
