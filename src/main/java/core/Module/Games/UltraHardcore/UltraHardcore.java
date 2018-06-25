package network.reborn.core.Module.Games.UltraHardcore;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import network.reborn.core.API.Module;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.Games.*;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.concurrent.TimeUnit;

public class UltraHardcore extends Game {
    private long startTime;
    private Map lobby;
    private World gameWorld;
    private long mapSize = 500;
    private boolean deathmatch = false;

    public UltraHardcore(RebornCore core) {
        super(core, "Ultra Hardcore", "ultra-hardcore", false, 2, new GameSettings(), Module.ULTRA_HARDCORE);
        setGameState(GameState.STARTING);
        setGameTemplate(GameTemplate.LAST_MAN_STANDING);
        getGameSettings().setGameLobby(new Location(Bukkit.getWorlds().get(0), -76, 100, -20));
        getGameSettings().setDefaultGameMode(GameMode.SURVIVAL);
        getGameSettings().setBuild(true);
        getGameSettings().setDestroy(true);
        getGameSettings().setPvp(false);
        getGameSettings().setPve(true);
        getGameSettings().setHunger(true);
        getGameSettings().setBuckets(true);
        getGameSettings().setDropItems(true);
        getGameSettings().setPickupItems(true);
        getGameSettings().setDropItemsOnDeath(true);
        getGameSettings().setDisableWeather(true);

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

        // Here we'll generate a new world for the UHC game!
        OtherUtil.deleteWorld("game");
        gameWorld = Bukkit.createWorld(new WorldCreator("game"));
        gameWorld.getWorldBorder().setCenter(gameWorld.getSpawnLocation().getBlockX(), gameWorld.getSpawnLocation().getBlockZ());
        gameWorld.getWorldBorder().setSize(mapSize * 2);
        gameWorld.getWorldBorder().setDamageBuffer(0);
        gameWorld.setGameRuleValue("spectatorsGenerateChunks", "false");
        gameWorld.setGameRuleValue("naturalRegeneration", "false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb game set " + mapSize + " " + mapSize + " " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb game fill 2500 0 true");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
        setGameState(GameState.STARTING);

        setMinPlayers(2);
        setMaxPlayers(20);
        startTimer();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getLocation().getWorld().getName().equals(lobby.getWorld().getName()))
            event.setCancelled(true); // Don't spawn entities in lobby
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (getGameState() == GameState.WAITING && event.getTo().getBlockY() < 22)
            event.getPlayer().teleport(getGameSettings().getGameLobby());
    }

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        super.onPlayerDamage(event);
        if (getGameState() != GameState.INGAME)
            return;
        if (getGameTime() < 10) { // No damage if the game has been going for less than 10 seconds! (Fall damage protection and just other things that could happen)
            event.setCancelled(true);
        }
    }

    public long getGameTime() {
        long current = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return current - startTime;
    }

    public void startGame() {
        freezeAllPlayers();
        getGameSettings().setDisableWeather(false);
        super.startGame();

        // Remove all entities that we don't want
        gameWorld.getEntities().stream().filter(entity -> entity.getType() != EntityType.ARMOR_STAND && !entity.isDead()).forEach(Entity::remove);
        gameWorld.setStorm(false);
        gameWorld.setThundering(false);
        gameWorld.setTime(0);

        for (final GamePlayer gamePlayer : getAlivePlayers()) {
            if (!gamePlayer.isOnline())
                return;
            Location location = gameWorld.getSpawnLocation();
            gamePlayer.getPlayer().teleport(location);
        }

        if (isTeams())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (100) + " " + (mapSize - 100) + " true @a ");
        else
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (100) + " " + (mapSize - 100) + " false @a ");

//		for (final GamePlayer gamePlayer : getAlivePlayers()) {
//			if (!gamePlayer.isOnline())
//				return;
//			Location location = gameWorld.getSpawnLocation();
//			Integer randX = OtherUtil.randInt(-((int) mapSize - 200), (int) mapSize - 200); // Randomly TP player anywhere within a 400 block radius of 0,0
//			Integer randZ = OtherUtil.randInt(-((int) mapSize - 200), (int) mapSize - 200);
//			location.setX(randX + 0.5);
//			location.setZ(randZ + 0.5); // Sets random location
//
//			Block block = location.getWorld().getHighestBlockAt(location); // Get Highest Block
//			Block spawnBlock = block.getLocation().add(0, -1, 0).getBlock();
//			location.setY(block.getY()); // Set highest block plus 3
//			location.setY(location.getY() + 8);
//			gamePlayer.getPlayer().teleport(location);
//			Material oldType = spawnBlock.getType();
//			spawnBlock.setType(Material.BEDROCK);
//			Bukkit.getScheduler().runTaskLater(getCore(), () -> spawnBlock.setType(oldType), 20 * 20L);
////			checkWater(gamePlayer.getPlayer()); // Check if player is in water etc
//		}

        startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        unfreezeAllPlayers();

        Bukkit.getScheduler().runTaskTimerAsynchronously(getCore(), () -> {
            if (getGameState() != GameState.INGAME)
                return;
            boolean pvpChange = false;
            int pvpTime = 60;
            if (getGameTime() == pvpTime && !getGameSettings().isPvp()) {
                getGameSettings().setPvp(true);
                pvpChange = true;
            }
            long remainingTime = 3600 - getGameTime();
//			long remainingTime = 120 - getGameTime();
            long timeTillPVP = pvpTime - getGameTime();

            if (remainingTime == 0 && !deathmatch) {
                deathmatch = true;
                if (isTeams())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (20) + " " + (100) + " true @a ");
                else
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " + gameWorld.getSpawnLocation().getBlockX() + " " + gameWorld.getSpawnLocation().getBlockZ() + " " + (20) + " " + (100) + " false @a ");
                gameWorld.getWorldBorder().setCenter(gameWorld.getSpawnLocation());
                gameWorld.getWorldBorder().setSize(240);
                gameWorld.getWorldBorder().setSize(50, 600);
                gameWorld.getWorldBorder().setDamageBuffer(0);
            } else if (remainingTime < 0) {
                return;
            }
            for (GamePlayer gamePlayer : getAlivePlayers()) {
                if (!gamePlayer.isOnline())
                    continue;
                if (getGameTime() >= pvpTime && getGameTime() <= (pvpTime + 4)) { // Show PVP has been enabled message for 4 seconds
                    if (pvpChange)
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 1F);
                    gamePlayer.sendActionBar(ChatColor.RED + "" + ChatColor.BOLD + "PVP has been enabled!");
                } else if (getGameTime() < pvpTime) {
                    gamePlayer.sendActionBar(ChatColor.RED + "Deathmatch: " + OtherUtil.getDurationString(remainingTime, true) + " PVP: " + OtherUtil.getDurationString(timeTillPVP, true));
                } else {
                    gamePlayer.sendActionBar(ChatColor.RED + "Deathmatch: " + OtherUtil.getDurationString(remainingTime, true));
                }
            }
        }, 10L, 10L);
    }

    @Override
    public Scoreboard getSoloScoreboard(Player player, boolean showKills, boolean showDeaths, boolean showPlayers) {
        return super.getSoloScoreboard(player, showKills, false, showPlayers);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Delete game world
        OtherUtil.deleteWorld("game");
    }

    @EventHandler
    public void onFillComplete(WorldBorderFillFinishedEvent event) {
        setGameState(GameState.WAITING);
    }

}
