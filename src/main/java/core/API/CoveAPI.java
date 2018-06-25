package network.reborn.core.API;

import network.reborn.core.Module.Games.Game;
import network.reborn.core.Module.Games.GamePlayer;
import network.reborn.core.Module.Games.SkyWars.SkyWars;
import network.reborn.core.Module.Games.TacticalAssault.TacticalAssault;
import network.reborn.core.Module.Games.UltraHardcore.UltraHardcore;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.Module.Module;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Database.MySQLManager;
import network.reborn.core.Util.Database.MySQLTask;
import network.reborn.core.Util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CoveAPI {
    public ArrayList<UUID> vanished = new ArrayList<>();
    private RebornCore rebornCore;
    private MySQLManager mySQLManager;
    private HashMap<UUID, RebornPlayer> players = new HashMap<>();
    private HashMap<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private network.reborn.core.Module.Module module = null;
    private Game game = null;

    public CoveAPI(RebornCore rebornCore) {
        this.rebornCore = rebornCore;
        try {
            mySQLManager = new MySQLManager(getRebornCore());
            new Thread(mySQLManager).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doVanishHides() {
        for (UUID uuid : vanished) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                vanished.remove(uuid);
                continue;
            }
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (player.equals(player1))
                    continue;
                player1.hidePlayer(player);
            }
        }
    }

    public RebornCore getRebornCore() {
        return rebornCore;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public void runSQLQueryPriority(final String sql) {
        getMySQLManager().schedulePriorityTask(new MySQLTask(getMySQLManager()) {
            @Override
            public void run() {
                Connection connection = manager.getNewConnection();
                try {
                    connection.createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null && !connection.isClosed())
                            connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void runSQLQuery(final String sql) {
        getMySQLManager().scheduleTask(new MySQLTask(getMySQLManager()) {
            @Override
            public void run() {
                Connection connection = manager.getNewConnection();
                try {
                    connection.createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null && !connection.isClosed())
                            connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public RebornPlayer getCovePlayer(Player player) {
        if (player == null) return null;
        return getCovePlayer(player.getUniqueId());
    }

    public RebornPlayer getCovePlayer(UUID uuid) {
        return getCovePlayer(uuid, true);
    }

    public RebornPlayer getCovePlayer(UUID uuid, boolean async) {
        if (players.containsKey(uuid))
            return players.get(uuid);
        RebornPlayer rebornPlayer = new RebornPlayer(uuid, async);
        players.put(uuid, rebornPlayer);
        return rebornPlayer;
    }

    public void removeCovePlayer(Player player) {
        if (player == null) return;
        removeCovePlayer(player.getUniqueId());
    }

    public void removeCovePlayer(UUID uuid) {
        if (players.containsKey(uuid))
            players.remove(uuid);
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        if (gamePlayers.containsKey(uuid))
            return gamePlayers.get(uuid);
        gamePlayers.put(uuid, new GamePlayer(uuid));
        return gamePlayers.get(uuid);
    }

    public GamePlayer getGamePlayer(Player player) {
        if (player == null)
            return null;
        return getGamePlayer(player.getUniqueId());
    }

    public void removeGamePlayer(UUID uuid) {
        if (gamePlayers.containsKey(uuid))
            gamePlayers.remove(uuid);
    }

    public ArrayList<RebornPlayer> getOnlineCovePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> getCovePlayer(player.getUniqueId())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<GamePlayer> getOnlineGamePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> getGamePlayer(player.getUniqueId())).collect(Collectors.toCollection(ArrayList::new));
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean downloadFile(String fileURL, String saveDir) throws IOException {
        boolean success = false;
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            }

            System.out.println("Content-Type: " + contentType);
            System.out.println("Content-Disposition: " + disposition);
            System.out.println("Content-Length: " + contentLength);
            System.out.println("Filename: " + fileName);

            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded successfully");
            success = true;
        } else {
            System.out.println("Failed to connect to URL, Response Code: " + responseCode);
        }
        httpConn.disconnect();
        return success;
    }

    public boolean extractZip(String zip, String dir) {
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        try {
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip));
            ZipEntry entry = null;
            entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = dir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dirr = new File(filePath);
                    dirr.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public void unregisterAllEvents() {
        HandlerList.unregisterAll(getRebornCore());
    }

    public void reloadModule() {
        // Remove custom enchantments
        /*
		if( EnchantmentAPI.getEnchantmentNames() != null && EnchantmentAPI.getEnchantmentNames().size() > 0 ) {
            for (String name : EnchantmentAPI.getEnchantmentNames()) {
                EnchantmentAPI.unregisterCustomEnchantment(name);
            }
        }*/

        // Unregister all listeners
        unregisterAllEvents();

        // Run reload of Core
//		onReload();

        // Register server listeners again
        getRebornCore().registerListeners();

        // Load module
        loadModule();
    }

    public void loadModule() {
        switch (getRebornCore().getConfig().getString("Module").toLowerCase().replaceAll("-", "").replaceAll("_", "").replaceAll(" ", "")) {
            default:
                break;
            case "hub":
                module = new Hub(getRebornCore());
                break;
            case "skywars":
                module = new SkyWars(getRebornCore());
                break;
            case "uhc":
            case "ultrahardcore":
                module = new UltraHardcore(getRebornCore());
                break;
            case "smp":
                module = new SMP(getRebornCore());
                break;
            case "tacticalassault":
                module = new TacticalAssault(getRebornCore());
                break;
            case "uhcreddit":
                module = new UltraHardcoreReddit(getRebornCore());
                break;
        }
    }

    public boolean updatePlayerRank(String player, String rank) {
        if (player == null || rank == null)
            return false;
        DonorRank donorRank = null;
        ServerRank serverRank = null;
        try {
            donorRank = DonorRank.valueOf(rank);
        } catch (Exception ignored) {
        }
        try {
            serverRank = ServerRank.valueOf(rank);
        } catch (Exception ignored) {
        }
        if (donorRank == null && serverRank == null)
            return false;

        if (player.length() > 16) {
            // UUID
            Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player));
            if (bukkitPlayer != null) {
                player = bukkitPlayer.getPlayer().getUniqueId().toString();
            }
            String sql;
            if (donorRank != null) {
                sql = "UPDATE `players` SET `DonorRank` = '" + donorRank.toString() + "' WHERE `UUID` = '" + player + "';";
            } else {
                sql = "UPDATE `players` SET `ServerRank` = '" + serverRank.toString() + "' WHERE `UUID` = '" + player + "';";
            }
            if (bukkitPlayer != null) {
                bukkitPlayer.kickPlayer(ChatColor.GREEN + "Rank updated! Please relog!");
            }
            runSQLQuery(sql);
            return true;
        } else {
            // Username
            String sql;
            Player bukkitPlayer = Bukkit.getPlayerExact(player);
            if (bukkitPlayer != null) {
                player = bukkitPlayer.getPlayer().getUniqueId().toString();
            } else {
                try {
                    player = UUIDFetcher.getUUIDOf(player).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (donorRank != null) {
                sql = "UPDATE `players` SET `DonorRank` = '" + donorRank.toString() + "' WHERE `UUID` = '" + player + "';";
            } else {
                sql = "UPDATE `players` SET `ServerRank` = '" + serverRank.toString() + "' WHERE `UUID` = '" + player + "';";
            }
            if (bukkitPlayer != null) {
                bukkitPlayer.kickPlayer(ChatColor.GREEN + "Rank updated! Please relog!");
            }
            runSQLQuery(sql);
            return true;
        }
    }

    public boolean setDefaultRank(String player, Boolean server) {
        if (player == null || server == null)
            return false;
        if (player.length() > 16) {
            // UUID
            Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player));
            if (bukkitPlayer != null) {
                player = bukkitPlayer.getPlayer().getUniqueId().toString();
            }
            String sql;
            if (!server) {
                sql = "UPDATE `players` SET `DonorRank` = 'DEFAULT' WHERE `UUID` = '" + player + "';";
            } else {
                sql = "UPDATE `players` SET `ServerRank` = 'DEFAULT' WHERE `UUID` = '" + player + "';";
            }
            if (bukkitPlayer != null) {
                bukkitPlayer.kickPlayer(ChatColor.GREEN + "Rank updated! Please relog!");
            }
            runSQLQuery(sql);
            return true;
        } else {
            // Username
            String sql;
            Player bukkitPlayer = Bukkit.getPlayerExact(player);
            if (bukkitPlayer != null) {
                player = bukkitPlayer.getPlayer().getUniqueId().toString();
            } else {
                try {
                    player = UUIDFetcher.getUUIDOf(player).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!server) {
                sql = "UPDATE `players` SET `DonorRank` = 'DEFAULT' WHERE `UUID` = '" + player + "';";
            } else {
                sql = "UPDATE `players` SET `ServerRank` = 'DEFAULT' WHERE `UUID` = '" + player + "';";
            }
            if (bukkitPlayer != null) {
                bukkitPlayer.kickPlayer(ChatColor.GREEN + "Rank updated! Please relog!");
            }
            runSQLQuery(sql);
            return true;
        }
    }
}
