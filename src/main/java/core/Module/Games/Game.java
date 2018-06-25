package network.reborn.core.Module.Games;

import com.google.common.collect.Lists;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.API.StatType;
import network.reborn.core.Events.PlayerDamageByPlayerEvent;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Events.PlayerRunJoinEvent;
import network.reborn.core.Events.RealPlayerMoveEvent;
import network.reborn.core.Listeners.PlayerMove;
import network.reborn.core.Module.Games.Events.KitUpdateEvent;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.Module.Module;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.ChestPopulator;
import network.reborn.core.Util.Database.MySQLTask;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

//import org.bukkit.attribute.Attribute;

@SuppressWarnings("WeakerAccess")
public abstract class Game extends Module implements Listener {
    protected static RebornCore core;
    public ArrayList<String> doneGG = new ArrayList<>();
    protected String gameTitle;
    protected String gameName;
    protected String description = "";
    protected GameSettings gameSettings;
    protected GameState gameState = GameState.STARTING;
    protected GameTemplate gameTemplate = null;
    protected ArrayList<String[]> chatMessages = new ArrayList<>();
    protected HashMap<String, Team> teams = new HashMap<>();
    protected HashMap<UUID, String> playerTeams = new HashMap<>();
    protected int minPlayers;
    protected int maxPlayers;
    protected int donorPlayers = 0;
    protected int timer = 60;
    protected int defaultTimer = 60;
    protected int gameID;
    protected boolean gamePrepared = false;
    protected int coinsPerKill = 5;
    protected int coinsPerWin = 50;
    protected boolean isTeams = false;
    protected Integer teamSize = 2;
    protected boolean teamCommandRegistered = false;
    protected PVPMode pvpMode = PVPMode.TRUE_1_8;
    protected KitManager kitManager = null;
    HashMap<String, Location> deaths = new HashMap<>();

    //TODO: Reimplement if version > 1.8
    /*	private void handlePVP() {
            switch (pvpMode) {
                default:
                case PVP_1_9:
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0D);
                    }
                    break;
                case PVP_1_8:
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);
                    }
                    break;
                case TRUE_1_8:
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);
                    }
                    break;
            }
        }

        private void handlePVP(Player player) {
            switch (pvpMode) {
                default:
                case PVP_1_9:
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0D);
                    break;
                case PVP_1_8:
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);
                    break;
                case TRUE_1_8:
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);
                    break;
            }
        }*/
    private List<Location> chestList = Lists.newArrayList();

    public Game(RebornCore core, String gameTitle, String gameName, Boolean teams, Integer teamSize, GameSettings gameSettings, network.reborn.core.API.Module module) {
        super(gameTitle, gameName, core, module);
        PlayerMove.setDoAfkStuff(false); // Don't do AFK stuff in games...
        Game.core = core;
        this.gameTitle = gameTitle;
        this.gameName = gameName;
        this.gameSettings = gameSettings;
        this.minPlayers = 2;
        this.maxPlayers = 2;
        this.isTeams = teams;
        this.teamSize = teamSize;
        gameSettings.setTeams(teams);
        gameSettings.setTeamSize(teamSize);

        core.getServer().getPluginCommand("game").setExecutor(new GameCommand(this));
        if (isTeams()) {
            teamCommandRegistered = true;
            core.getServer().getPluginCommand("team").setExecutor(new TeamCommand(this));
        }
        setGameState(GameState.WAITING);
        RebornCore.getCoveAPI().setGame(this);
        //TODO: On 1.9+ enable this
        //handlePVP();
    }

    public static RebornCore getCore() {
        return core;
    }

    public void changePVPMode(PVPMode pvpMode) {
        this.pvpMode = pvpMode;
        //TODO: On 1.9+ enable this
        //handlePVP();
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getGameName() {
        return gameName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (getCoveServer() == null)
            return;
        String sql = "UPDATE `Servers` SET `GameState` = '" + gameState.toString() + "' WHERE `ID` = " + getCoveServer().getID() + ";";
        RebornCore.getCoveAPI().runSQLQueryPriority(sql);
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        if (RebornCore.getCoveAPI().getModule() != null && RebornCore.getCoveAPI().getModule().getCoveServer() != null) {
            String sql = "UPDATE `Servers` SET `MaxPlayers` = " + maxPlayers + " WHERE `ID` = " + RebornCore.getCoveAPI().getModule().getCoveServer().getID() + ";";
            RebornCore.getCoveAPI().runSQLQuery(sql);
        }
        this.maxPlayers = maxPlayers;
    }

    public int getDonorPlayers() {
        return donorPlayers;
    }

    public void setDonorPlayers(int donorPlayers) {
        this.donorPlayers = donorPlayers;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getDefaultTimer() {
        return defaultTimer;
    }

    public void setDefaultTimer(int defaultTimer) {
        this.defaultTimer = defaultTimer;
    }

    public boolean isGamePrepared() {
        return gamePrepared;
    }

    public void setGamePrepared(boolean gamePrepared) {
        this.gamePrepared = gamePrepared;
    }

    public boolean isTeams() {
        return isTeams;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void freezeAllPlayers() {
        getGameSettings().setFrozen(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setFlySpeed(0);
            player.setWalkSpeed(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -5));
        }
    }

    public void unfreezeAllPlayers() {
        getGameSettings().setFrozen(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setFlySpeed((float) 0.1);
            player.setWalkSpeed((float) 0.2);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
    }

    public void startTimer() {
        Bukkit.getScheduler().runTaskTimer(getCore(), this::doTimer, 20L, 20L);
    }

    public void doTimer() {
        if (getGameState() == GameState.INGAME || getGameState() == GameState.FINISHED)
            return;

        if (Bukkit.getOnlinePlayers().size() < getMinPlayers()) {
            setTimer(getDefaultTimer());
            return;
        }

        if (getGameState() == GameState.STARTING)
            setGameState(GameState.WAITING);

        if (getTimer() == 0) {
            startGame();
        } else if (getTimer() <= 1) {
            for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
                if (!rebornPlayer.isOnline())
                    continue;
                rebornPlayer.getPlayer().setLevel(getTimer());
                rebornPlayer.getPlayer().setExp(0);
                rebornPlayer.sendTitle(ChatColor.GOLD + "Game Starting In", ChatColor.RED.toString() + getTimer(), 0, 40, 0);
            }
        } else if (getTimer() <= 5) {
            for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
                if (!rebornPlayer.isOnline())
                    continue;
                rebornPlayer.getPlayer().setLevel(getTimer());
                rebornPlayer.getPlayer().setExp(0);
                rebornPlayer.sendTitle(ChatColor.GOLD + "Game Starting In", ChatColor.YELLOW.toString() + getTimer(), 0, 40, 0);
            }
        } else if (getTimer() <= 10) {
            for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
                if (!rebornPlayer.isOnline())
                    continue;
                rebornPlayer.getPlayer().setLevel(getTimer());
                rebornPlayer.getPlayer().setExp(0);
                rebornPlayer.sendTitle(ChatColor.GOLD + "Game Starting In", ChatColor.GREEN.toString() + getTimer(), 0, 40, 0);
            }
            if (getTimer() == 10) {
                prepareGame();
            }
        } else if (getTimer() <= 30) {
            for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
                rebornPlayer.sendActionBar(ChatColor.GOLD + "Starting in " + getTimer() + "...");
                rebornPlayer.getPlayer().setLevel(getTimer());
                rebornPlayer.getPlayer().setExp(0);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setLevel(getTimer());
                player.setExp(0);
            }
        }

        setTimer(getTimer() - 1);
    }

    public void startGame() {
        /** This method runs when either an admin+ starts the game or when the timer hits 0 for game start */
        if (Bukkit.getOnlinePlayers().size() == 0) Bukkit.shutdown();
        if (getGameState() == GameState.INGAME) return;
        setGameState(GameState.INGAME);

        final String sql = "INSERT INTO `Games` (`GameName`,`StartTime`) VALUES ('" + getGameName() + "','" + OtherUtil.getCurrentDateToMySQL() + "');";

        String[] chat = new String[2];
        chat[0] = "Event";
        chat[1] = "Game Started!";
        chatMessages.add(chat);

        RebornCore.getCoveAPI().getMySQLManager().scheduleTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                try {
                    setGameID(manager.getConnection().createStatement().executeUpdate(sql, Statement.RETURN_GENERATED_KEYS));
                    ResultSet resultSet = manager.getConnection().createStatement().executeQuery("SELECT LAST_INSERT_ID() AS LastID FROM `Games`");
                    if (resultSet.next()) setGameID(resultSet.getInt("LastID"));

                    String sql2 = "INSERT INTO `GamePlayers` (`GameID`,`PlayerUUID`,`PlayerName`) VALUES ";
                    int i = 0;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sql2 = sql2 + "(" + getGameID() + ",'" + player.getUniqueId().toString().replaceAll("-", "") + "','" + player.getName() + "')";
                        if (i + 1 != Bukkit.getOnlinePlayers().size()) {
                            sql2 = sql2 + ", ";
                        }
                        i++;
                    }
                    manager.getConnection().createStatement().execute(sql2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Sort inventory's and kits etc.
        for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
            rebornPlayer.getPlayer().setMaxHealth(20);
            rebornPlayer.getPlayer().setHealth(20);
            rebornPlayer.getPlayer().setSaturation(20);
            rebornPlayer.getPlayer().setFoodLevel(20);
            rebornPlayer.getPlayer().closeInventory();
            rebornPlayer.getPlayer().closeInventory();
            rebornPlayer.getPlayer().getInventory().clear();
            rebornPlayer.getPlayer().setLevel(0);
            rebornPlayer.getPlayer().setExp(0);
            PlayerDisguise playerDisguise = new PlayerDisguise(rebornPlayer.getName());
            DisguiseAPI.disguiseToAll(rebornPlayer.getPlayer(), playerDisguise);
            rebornPlayer.getPlayer().setGameMode(getGameSettings().getDefaultGameMode());
            addStats(rebornPlayer.getPlayer(), StatType.PLAY, 1, null);
            if (!getDescription().equals("")) {
                rebornPlayer.sendCentredMessage("");
                rebornPlayer.sendCentredMessage(getDescription());
                rebornPlayer.sendCentredMessage("");
            }
        }

        if (getGameSettings().isGiveKitsOnStart() && kitManager != null)
            kitManager.giveAllPlayersKits();

        if (isTeams()) {
            // First we need to register all the teams with scoreboards
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

            for (Map.Entry<String, Team> team : teams.entrySet()) {
                org.bukkit.scoreboard.Team team1 = scoreboard.registerNewTeam(team.getKey());
                for (UUID uuid : team.getValue().getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        continue;
                    team1.addPlayer(player);
                }
                team1.setPrefix(team.getValue().getTeamColor() + "");
                team1.setAllowFriendlyFire(false);
//				team.getValue().doNametags(); // Hopefully shouldn't be needed!
            }
        } else {
            doAllScoreboards();
        }
    }

    public void prepareGame() {
        /** This method is usually best for map loading or anything that may take a little while... It runs 10 seconds before start */
        if (isGamePrepared()) // Stop game preparation from ever running more than once
            return;
        setGamePrepared(true);
    }

    public void endGame(String winner) {
        setGameState(GameState.FINISHED);
        for (Player player : Bukkit.getOnlinePlayers())
            player.setGameMode(GameMode.ADVENTURE);
        String[] chat = new String[2];
        chat[0] = "Event";
        Player winnerPlayer = null;
        if (winner != null && Bukkit.getPlayerExact(winner) != null)
            winnerPlayer = Bukkit.getPlayerExact(winner);

        List<String> messages = new ArrayList<>();
        messages.add(ChatColor.GREEN + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messages.add(ChatColor.WHITE + "" + ChatColor.BOLD + getGameTitle() + " Game Summary");
        messages.add("");
        if (winnerPlayer != null) {
            String color;
            RebornPlayer legacyWinnerPlayer = RebornCore.getCoveAPI().getCovePlayer(winnerPlayer);
            if (legacyWinnerPlayer.getServerRank() != ServerRank.DEFAULT)
                color = legacyWinnerPlayer.getServerRank().getChatColor() + "";
            else
                color = legacyWinnerPlayer.getDonorRank().getChatColor() + "";
            messages.add(ChatColor.YELLOW + "Winner: " + color + winnerPlayer.getDisplayName());
            RebornCore.getCoveAPI().getGamePlayer(winnerPlayer).giveBalance(getSlug(), getCoinsPerWin(), true, true, ChatColor.GOLD + "+%amount% Coins");
            messages.add("");
        } else if (winner != null && !winner.equals("")) {
            messages.add(ChatColor.YELLOW + "Winner: " + winner);
            messages.add("");
        }

        List<String> extraMessages = getExtraMessages();
        if (extraMessages.size() > 0) {
            messages.addAll(extraMessages);
            messages.add("");
        }

        messages.add(ChatColor.GREEN + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        String[] messageArray = new String[messages.size()];
        messageArray = messages.toArray(messageArray);
        sendEndGameMessages(true, messageArray);

        if (winnerPlayer != null) {
            addStats(winnerPlayer, StatType.WIN, 1, null);
            chat[1] = "Game Finished (Winner " + winnerPlayer.getName() + ")";
        } else if (winner != null && !winner.equals("")) {
            chat[1] = "Game Finished (Winner " + ChatColor.stripColor(winner) + ")";
        } else {
            chat[1] = "Game Finished";
        }
        chatMessages.add(chat);

        final String sql = "UPDATE `Games` SET `EndTime` = '" + OtherUtil.getCurrentDateToMySQL() + "', `Winner` = '" + winner + "' WHERE `ID` = " + getGameID() + ";";

        RebornCore.getCoveAPI().getMySQLManager().scheduleTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                try {
                    manager.getConnection().createStatement().executeUpdate(sql);

                    if (chatMessages.size() < 1)
                        return;

                    // Save chat log
                    String sql2 = "INSERT INTO `GameChat` (`GameID`,`IsEvent`,`PlayerUUID`,`Message`) VALUES ";

                    int i = 0;
                    for (String[] strings : chatMessages) {
                        int isEvent = 0;
                        if (strings[0].equalsIgnoreCase("Event"))
                            isEvent = 1;
                        sql2 = sql2 + "(" + getGameID() + "," + isEvent + ",'" + strings[0] + "',?)";
                        if (i + 1 != chatMessages.size()) {
                            sql2 = sql2 + ", ";
                        }
                        i++;
                    }

                    PreparedStatement preparedStatement = manager.getConnection().prepareStatement(sql2);
                    i = 1;
                    for (String[] strings : chatMessages) {
                        preparedStatement.setString(i, strings[1]);
                        i++;
                    }
                    preparedStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        for (RebornPlayer p : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
            p.sendToRandomHub();
        }
        //Bukkit.getScheduler().runTaskLater(getCore(), () -> RebornCore.getCoveAPI().getOnlineCovePlayers().forEach(RebornPlayer::sendToRandomHub), 10 * 20L);
        //Bukkit.getScheduler().runTaskLater(getCore(), () -> RebornCore.getCoveAPI().getOnlineCovePlayers().forEach(RebornPlayer::sendToRandomHub), 15 * 20L);
        Bukkit.getScheduler().runTaskLater(getCore(), Bukkit::shutdown, 20 * 20L);
    }

    public void sendEndGameMessages(boolean centered, String... messages) {
        for (RebornPlayer rebornPlayer : RebornCore.getCoveAPI().getOnlineCovePlayers()) {
            for (String message : messages) {
                message = perPlayerMessageConvert(message, rebornPlayer);
                if (centered)
                    rebornPlayer.sendCentredMessage(message);
                else if (rebornPlayer.isOnline())
                    rebornPlayer.getPlayer().sendMessage(message);
            }
        }
    }

    public String perPlayerMessageConvert(String message, RebornPlayer rebornPlayer) {
        GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(rebornPlayer.getUUID());
        message = message.replaceAll("%kills%", String.valueOf(gamePlayer.getKills()));
        message = message.replaceAll("%deaths%", String.valueOf(gamePlayer.getDeaths()));
        message = message.replaceAll("%coins%", String.valueOf(gamePlayer.getEarnedCoins()));
//        message = message.replaceAll("%kdr%", String.valueOf(gamePlayer.getKDR()));
        message = message.replaceAll("%kdr%", "TODO");
        return message;
    }

    public List<String> getExtraMessages() {
        // Override in games
        return new ArrayList<>();
    }

    @Deprecated
    public void addStats(final Player player, final StatType statType, final int amount, final String other) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        rebornPlayer.addStat(gameName.replaceAll(" ", "_"), statType, amount, other);
    }

    public Scoreboard getLobbyScoreboard(Player player) {
        if (getGameState() != GameState.WAITING) return null; // Only for lobby's
        Scoreboard scoreboard;
        if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        scoreboard.getObjectives().clear();
        Objective objective = scoreboard.registerNewObjective("lobby", "dummy");
        objective.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + getGameTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score;

        // if (kitManager.hasKitsSetup()) {
        score = objective.getScore("" + ChatColor.BLACK);
        score.setScore(7);

//            String kit = LoxCore.getCurrentGame().kitManager.getActiveKit(player);
        if (kitManager != null) {
            if (kitManager.getPlayersKit(player) == null) {
                score = objective.getScore(ChatColor.GREEN + "Kit: " + ChatColor.WHITE + "None");
            } else {
                score = objective.getScore(ChatColor.GREEN + "Kit: " + ChatColor.WHITE + kitManager.getPlayersKit(player).getName());
            }
            score.setScore(6);
        }

        score = objective.getScore("" + ChatColor.BLACK + ChatColor.BLACK + ChatColor.BLACK);
        score.setScore(5);
        //} else {
        //    score = objective.getScore("" + ChatColor.BLACK);
        //    score.setScore(5);
        //}

        score = objective.getScore(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + RebornCore.getCoveAPI().getCovePlayer(player.getUniqueId()).getBalance(getSlug()));
        score.setScore(4);

        score = objective.getScore("" + ChatColor.WHITE + ChatColor.WHITE);
        score.setScore(3);

        score = objective.getScore(ChatColor.LIGHT_PURPLE + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size() + "/" + getMaxPlayers());
        score.setScore(2);

        score = objective.getScore("" + ChatColor.WHITE);
        score.setScore(1);

        return scoreboard;
    }

    public Scoreboard getSoloScoreboard(Player player, boolean showKills, boolean showDeaths, boolean showPlayers) {
        if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
            return Bukkit.getScoreboardManager().getMainScoreboard();
        }
        Scoreboard scoreboard;
        if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        Objective objective = scoreboard.registerNewObjective("lobby", "dummy");
        objective.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + getGameTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (getGameSettings().showHealthInTab()) {
            Objective health = scoreboard.registerNewObjective("Health", "health");
            health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        Score score;

        GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(player);
        if (gamePlayer == null || !gamePlayer.isOnline())
            return null;

        int currentScore = 1;
        score = objective.getScore("" + ChatColor.BLACK);
        score.setScore(currentScore);
        currentScore++;

        if (showDeaths) {
            score = objective.getScore("" + ChatColor.WHITE + "Deaths: " + ChatColor.AQUA + gamePlayer.getDeaths());
            score.setScore(currentScore);
            currentScore++;

            score = objective.getScore("" + ChatColor.BLACK + ChatColor.BLACK);
            score.setScore(currentScore);
            currentScore++;
        }

        if (showKills) {
            score = objective.getScore("" + ChatColor.WHITE + "Kills: " + ChatColor.AQUA + gamePlayer.getKills());
            score.setScore(currentScore);
            currentScore++;

            score = objective.getScore("" + ChatColor.BLACK + ChatColor.BLACK + ChatColor.BLACK);
            score.setScore(currentScore);
            currentScore++;
        }

        if (showPlayers) {
            score = objective.getScore("" + ChatColor.WHITE + "Players: " + ChatColor.AQUA + getAlivePlayers().size());
            score.setScore(currentScore);
            currentScore++;

            score = objective.getScore("" + ChatColor.BLACK + ChatColor.BLACK + ChatColor.BLACK + ChatColor.BLACK);
            score.setScore(currentScore);
            currentScore++;
        }

        return scoreboard;
    }

    public void doGameScoreboard(Player player) {
        if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
            return;
        }
        Scoreboard scoreboard = getSoloScoreboard(player, true, true, true);
        if (scoreboard != null)
            player.setScoreboard(scoreboard);
    }

    public void doAllScoreboards() {
        if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
            return;
        }
        if (getGameState() == GameState.WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = getLobbyScoreboard(player);
                if (scoreboard != null)
                    player.setScoreboard(scoreboard);
            }
        } else {
            Bukkit.getOnlinePlayers().forEach(this::doGameScoreboard);
        }

    }

    public HashMap<String, Team> getTeams() {
        return teams;
    }

    public void setTeams(boolean isTeams) {
        this.isTeams = isTeams;
        if (!teamCommandRegistered) {
            teamCommandRegistered = true;
            core.getServer().getPluginCommand("team").setExecutor(new TeamCommand(this));
        }
    }

    public HashMap<UUID, String> getPlayerTeams() {
        return playerTeams;
    }

    public Team getPlayerTeam(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId()))
            return null;
        String teamSlug = playerTeams.get(player.getUniqueId());
        if (!teams.containsKey(teamSlug))
            return null;
        return teams.get(teamSlug);
    }

    public GameTemplate getGameTemplate() {
        return gameTemplate;
    }

    public void setGameTemplate(GameTemplate gameTemplate) {
        this.gameTemplate = gameTemplate;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (getGameState().equals(GameState.STARTING) /*&& !rebornPlayer.canPlayer(ServerRank.DEVELOPER)*/) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            if (RebornCore.getCoveAPI().getGame().getModule().equals(network.reborn.core.API.Module.UHC_REDDIT)) {
                event.setKickMessage(ChatColor.RED + "World is still pregenerating. " + ChatColor.YELLOW + "Completed: " + ChatColor.GOLD + UltraHardcoreReddit.loadedPercent);
            } else {
                event.setKickMessage(ChatColor.RED + "This game is still starting!");
            }
        } else if (getGameState() != GameState.WAITING && !rebornPlayer.canPlayer(ServerRank.HELPER) && !Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(event.getPlayer().getName()))) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            event.setKickMessage(ChatColor.RED + "This game has already started");
        } else if (Bukkit.getOnlinePlayers().size() >= maxPlayers && !rebornPlayer.canPlayer(ServerRank.HELPER)) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            event.setKickMessage(ChatColor.RED + "This game is full");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (gameState == GameState.WAITING) {
            if (getGameSettings().getGameLobby() != null) {
                player.teleport(getGameSettings().getGameLobby());
            }
        }
        //TODO: If on 1.9+, reenable this
        //handlePVP(player);
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);

        int serverID = 0;
        if (getCoveServer() != null)
            serverID = getCoveServer().getID();
        String header = ChatColor.GREEN + "You are playing: " + ChatColor.BOLD + getGameTitle() + ChatColor.GREEN + " - Server: #" + serverID;
        String footer = ChatColor.GOLD + "        Check out our forums at reborn.network          ";
        rebornPlayer.sendTabTitle(header, footer);

        PlayerDisguise playerDisguise = new PlayerDisguise(rebornPlayer.getName());
        DisguiseAPI.disguiseToAll(event.getPlayer(), playerDisguise);

        if (gameState == GameState.WAITING) {
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setLevel(0);
            player.setExp(0);
            player.getInventory().setHeldItemSlot(0);

            if (kitManager != null && kitManager.getRegisteredKits().size() > 1 && getGameSettings().isIncludeKitSelector()) {
                ItemStack kitSelector = new ItemStack(Material.BOW);
                ItemMeta kitSelectorMeta = kitSelector.getItemMeta();
                kitSelectorMeta.setDisplayName(ChatColor.GREEN + "Kit Selector" + ChatColor.GRAY + " (Right Click)");
                kitSelector.setItemMeta(kitSelectorMeta);
                player.getInventory().addItem(kitSelector);
            }

            if (false) { // *cough* actually will be maps when we add voting *cough*
                ItemStack mapSelector = new ItemStack(Material.EMPTY_MAP);
                ItemMeta mapSelectorMeta = mapSelector.getItemMeta();
                mapSelectorMeta.setDisplayName(ChatColor.GREEN + "Map Voting" + ChatColor.GRAY + " (Right Click)");
                mapSelector.setItemMeta(mapSelectorMeta);
                player.getInventory().addItem(mapSelector);
            }

            // Back to lobby item
            ItemStack lobby = new ItemStack(Material.REDSTONE);
            ItemMeta lobbyMeta = lobby.getItemMeta();
            lobbyMeta.setDisplayName(ChatColor.GREEN + "Back to Lobby" + ChatColor.GRAY + " (Right Click)");
            lobby.setItemMeta(lobbyMeta);
            player.getInventory().setItem(8, lobby);

            event.setJoinMessage(null);
//			event.setJoinMessage(rebornPlayer.getName() + ChatColor.GOLD + " has joined the game (" + Bukkit.getOnlinePlayers().size() + "/" + getMaxPlayers() + ")");
        } else if (getModule().equals(network.reborn.core.API.Module.UHC_REDDIT) && getGameState().equals(GameState.INGAME) && Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())) && !((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).isDeathmatch()) {
            event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has rejoined the game");
        } else {
            event.setJoinMessage(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            if (!getGameSettings().isHunger())
                player.setFoodLevel(20);
        }

        if (getGameSettings().isFrozen()) {
            player.setFlySpeed((float) 0);
            player.setWalkSpeed((float) 0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -20));
        } else {
            // Just incase they are frozen from last logout of server
            player.setFlySpeed((float) 0.1);
            player.setWalkSpeed((float) 0.2);
            player.removePotionEffect(PotionEffectType.JUMP);
        }

        doAllScoreboards();
    }

    @EventHandler
    public void onPlayerRunJoin(PlayerRunJoinEvent event) {
        Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.GOLD + " has joined the game (" + Bukkit.getOnlinePlayers().size() + "/" + getMaxPlayers() + ")");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (gameState == GameState.WAITING) {
            event.setQuitMessage(rebornPlayer.getName() + ChatColor.GOLD + " has left the game (" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + maxPlayers + ")");
        } else if (gameState != GameState.INGAME) {
            event.setQuitMessage(rebornPlayer.getName() + ChatColor.GOLD + " has left the game");
        }
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
            doAllScoreboards();
            checkIfLastManStanding();
        }, 5L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(PlayerDamageEvent event) {
        if (!getGameSettings().isDamage() || getGameState() != GameState.INGAME)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
        if (!getGameSettings().isPvp() || getGameState() != GameState.INGAME)
            event.setCanceled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow a = (Arrow) event.getDamager();
            if (a.getShooter() instanceof Player) {
                if (!getGameSettings().isPvp()) {
                    event.setCancelled(true);
                }
            }
        }
        if (!(event.getEntity() instanceof Player) || event.getDamager() instanceof Player)
            return;
        if (!getGameSettings().isPve() && event.getEntity() instanceof Monster)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.getEntity() instanceof Player)
            return;

        if (getGameState() != GameState.INGAME || !getGameSettings().isPve())
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        deaths.put(player.getUniqueId().toString().replaceAll("-", ""), player.getLocation());
        if (!getGameSettings().isDropItemsOnDeath()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

        if (getGameSettings().isForceRespawn()) {
            long delay = 5L;
            if (getGameSettings().isDeadBodies())
                delay = 10L; // Makes body fall (Looks Nicer)
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCore(), new Runnable() {
                public void run() {
                    player.spigot().respawn();
//					if (getGameSettings().isDeadBodies())// TODO
//						RebornCore.getRebornAPI().spawnDeadBody(player, null);
                }
            }, delay);
        }

        event.setDeathMessage(ChatColor.RED + event.getDeathMessage());
        RebornCore.getCoveAPI().getGamePlayer(player).addDeath();

        if (getGameState() != GameState.INGAME) {
            event.setDeathMessage(null);
        } else {
            event.setDeathMessage(ChatColor.RED + event.getEntity().getName() + " died");
        }
        if (player.getKiller() != null && !Objects.equals(player.getKiller().getName(), player.getName())) { // Stop suicides O.o
            RebornCore.getCoveAPI().getGamePlayer(player.getKiller()).addKill();
            RebornCore.getCoveAPI().getGamePlayer(player.getKiller()).giveBalance(getSlug(), getCoinsPerKill(), true, true, ChatColor.GOLD + "+%amount% Coins");
            Scoreboard scoreboard = getSoloScoreboard(player.getKiller(), true, true, true);
            if (scoreboard != null)
                player.getKiller().setScoreboard(scoreboard);
            player.getKiller().sendMessage(ChatColor.GOLD + "You killed " + event.getEntity().getDisplayName());
        }
        Scoreboard scoreboard = getSoloScoreboard(player, true, true, true);
        if (scoreboard != null)
            player.setScoreboard(scoreboard);

        if (getGameTemplate() == GameTemplate.LAST_MAN_STANDING && getGameState() == GameState.INGAME) {
            GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(player);
            gamePlayer.setSpectator(true);
            checkIfLastManStanding();
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public void checkIfLastManStanding() {
        int online = getAlivePlayers().size();
        if (getGameTemplate() == GameTemplate.LAST_MAN_STANDING && getGameState() == GameState.INGAME) {
            if (online == 1) {
                endGame(getAlivePlayers().get(0).getPlayer().getName());
            } else if (online == 0) {
                endGame(null);
            }
        }
    }

    /**
     * @return An ArrayList of GamePlayer's that are no longer part of the game and are spectators
     */
    public ArrayList<GamePlayer> getSpectators() {
        return RebornCore.getCoveAPI().getOnlineGamePlayers().stream().filter(GamePlayer::isSpectator).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return An ArrayList of GamePlayer's that are still alive and not a spectator
     */
    public ArrayList<GamePlayer> getAlivePlayers() {
        return RebornCore.getCoveAPI().getOnlineGamePlayers().stream().filter(gamePlayer -> !gamePlayer.isSpectator()).collect(Collectors.toCollection(ArrayList::new));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
        if (getGameState() != GameState.WAITING && !getGameSettings().isChatInLobby()) {
            event.setCancelled(true);
            return;
        } else if (!getGameSettings().isChatInGame()) {
            event.setCancelled(true);
            return;
        } else if (getGameState() == GameState.FINISHED && (event.getMessage().toLowerCase().equals("gg") || event.getMessage().toLowerCase().startsWith("gg") || event.getMessage().toLowerCase().contains(" gg ") || event.getMessage().toLowerCase().endsWith("gg")) && !doneGG.contains(event.getPlayer().getName())) {
            doneGG.add(event.getPlayer().getName());
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
            rebornPlayer.giveBalance("NetworkXP", 25, false, true, ChatColor.GREEN + "+%amount% XP"); // +25 XP for GG
        }

        // Log chat to hashmap
        if (!event.isCancelled()) {
            String[] chat = new String[2];
            chat[0] = event.getPlayer().getUniqueId().toString().replaceAll("-", "");
            chat[1] = event.getMessage();
            chatMessages.add(chat);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRealPlayerMove(RealPlayerMoveEvent event) {
        if (getGameSettings().isFrozen())
            event.setCanceled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (getGameState() != GameState.INGAME || !getGameSettings().isHunger())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (getGameState() != GameState.INGAME || !getGameSettings().isDropItems())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (getGameState() != GameState.INGAME || !getGameSettings().isPickupItems())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((getGameState() != GameState.INGAME || !getGameSettings().isDestroy()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) // TODO Check If Staff (Admin)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if ((getGameState() != GameState.INGAME || !getGameSettings().isBuild()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) // TODO Check If Staff (Admin)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBucketUse(PlayerInteractEvent event) {
        if (getGameState() == GameState.INGAME && !getGameSettings().isBuckets() && event.getItem() != null && event.getItem().getType().toString().toUpperCase().contains("BUCKET"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !event.getItem().hasItemMeta())
            return;

        if (!event.getAction().toString().toUpperCase().contains("RIGHT"))
            return;

        if (getGameState() != GameState.WAITING)
            return;

        if (event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Kit Selector")) {
            event.setCancelled(true);
            if (kitManager != null)
                kitManager.openKitGUI(event.getPlayer());
        } else if (event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Map Selector")) {
            event.setCancelled(true);
            if (isGamePrepared()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Map has already been selected and loaded");
            } else {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "// TODO: Make maps work xD"); // TODO
            }
            event.getPlayer().updateInventory();
        } else if (event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Back to Lobby")) {
            event.setCancelled(true);
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
            rebornPlayer.sendToRandomHub();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKitUpdate(KitUpdateEvent event) {
        event.getPlayer().setScoreboard(getLobbyScoreboard(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest) {
            if (event.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest chest = (DoubleChest) event.getInventory().getHolder();
                Location location = chest.getLocation();
                if (chestList.contains(location) || getGameState() != GameState.INGAME || !getGameSettings().isAutoPopulateChests())
                    return;
                chestList.add(location);
                ChestPopulator.get().populateDoubleChest(chest);
            } else {
                Chest chest = (Chest) event.getInventory().getHolder();
                Location location = chest.getLocation();
                if (chestList.contains(location) || getGameState() != GameState.INGAME || !getGameSettings().isAutoPopulateChests())
                    return;
                chestList.add(location);
                ChestPopulator.get().populateChest(chest);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getKiller() != null) {
            event.setRespawnLocation(player.getKiller().getLocation());
        } else if (deaths.containsKey(player.getUniqueId().toString().replaceAll("-", ""))) {
            event.setRespawnLocation(deaths.get(player.getUniqueId().toString().replaceAll("-", "")));
        }
        if (getGameTemplate() != null && getGameTemplate() == GameTemplate.LAST_MAN_STANDING)
            player.setGameMode(GameMode.SPECTATOR);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public int getCoinsPerKill() {
        return coinsPerKill;
    }

    public void setCoinsPerKill(int coinsPerKill) {
        this.coinsPerKill = coinsPerKill;
    }

    public int getCoinsPerWin() {
        return coinsPerWin;
    }

    public void setCoinsPerWin(int coinsPerWin) {
        this.coinsPerWin = coinsPerWin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (getGameSettings().isDisableWeather() || getGameState() != GameState.INGAME)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (RebornCore.getCoveAPI().getGame().getGameState() == GameState.INGAME && event.getTo().getY() < 0) {
            event.getPlayer().damage(5000);
        }
    }

    //TODO: Enable for 1.9+
    /*@EventHandler(priority = EventPriority.LOW)
    public void onItemSwitch(PlayerSwapHandItemsEvent event) {
		if (pvpMode == PVPMode.TRUE_1_8)
			event.setCancelled(true);
	}*/

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        // TODO disable offhand slot for 1.9+

    }
}
