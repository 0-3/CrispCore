package network.reborn.core.Module.Games.UltraHardcoreReddit.Database;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameData;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameId;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.PlayerStats;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.RedditEngine;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RedditDatabase {

    private Connection db;
    private PreparedStatement insertData;
    private PreparedStatement insertDefaultStats;
    private PreparedStatement updateStats;
    private PreparedStatement getAllValues;
    private PreparedStatement getAllStats;
    private PreparedStatement getStats;
    private List<GameData> archives = new ArrayList<GameData>();
    private PreparedStatement getLastId;

    /*public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Match Post " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }*/

    public static void messagePlayer(Player p, String message) {
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Archive " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    public void onEnable() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            db = DriverManager.getConnection("jdbc:mysql://" + "192.99.18.32" + ":"
                    + "3306" + "/" + "uhcdata", "root", "20MF70cmh");

            Statement statement = db.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + "RedditData"
                    + "(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, host_uuid VARCHAR(45), winner_uuid VARCHAR(45), datetime VARCHAR(30), scenarios VARCHAR(500), matchpost VARCHAR(500))");
            Statement statDB = db.createStatement();
            statDB.execute("CREATE TABLE IF NOT EXISTS " + "RedditStats" + "(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(45), wins INT, losses INT, kills INT)");
            insertData = db.prepareStatement(
                    "INSERT INTO RedditData(host_uuid, winner_uuid, datetime, scenarios, matchpost) VALUES (?,?,?,?,?)");
            //Only sets uuid to prevent overriding stats in case of failed db connection
            insertDefaultStats = db.prepareStatement("INSERT INTO RedditStats(uuid) VALUES (?)");
            //Arg0: Wins
            //Arg1: Losses
            //Arg2: Kills
            //Arg3: UUID
            updateStats = db.prepareStatement("UPDATE RedditStats set wins = ?, losses = ?, kills = ? WHERE uuid = ?");
            getStats = db.prepareStatement("SELECT * FROM RedditStats WHERE uuid = ?");
            getAllValues = db
                    .prepareStatement("SELECT * from RedditData order by id");
            getAllStats = db.prepareStatement("SELECT * from RedditStats order by id");
            getLastId = db.prepareStatement("SELECT * FROM RedditData ORDER BY id DESC LIMIT 1");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(),
                new DatabaseKeepAliveTask(), 1200L, 1200L);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(RebornCore.getRebornCore(),
                new PurgeDuplicateDataTask(), 1200L, 1200L);
    }

    public void onDisable() {
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addToArchive(String host_uuid, String winner_uuid, String datetime,
                                          String scenarios, String matchURL) {
        GameId.incrementValue();
        try {
            insertData.setString(1, host_uuid);
            insertData.setString(2, winner_uuid);
            insertData.setString(3, datetime);
            insertData.setString(4, scenarios);
            insertData.setString(5, matchURL);
            insertData.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setDefaultData(String uuid) {
        try {
            insertDefaultStats.setString(1, uuid);
            insertDefaultStats.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateStats(String uuid, int wins, int losses, int kills) {
        try {
            updateStats.setInt(1, wins);
            updateStats.setInt(2, losses);
            updateStats.setInt(3, kills);
            updateStats.setString(4, uuid);
            updateStats.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerStats getStats(String uuid) {
        try {
            getStats.setString(1, uuid);
            ResultSet rs = getStats.executeQuery();
            while (rs.next()) {
                //ResultSet automatically converts a null int to 0
                return new PlayerStats(rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("It appears as if either the DB is offline or this UUID has no attached data.");
            //Drop the stacktrace - this error will occur commonly as new players join and populate the DB.
            //e.printStackTrace();
        }
        return new PlayerStats("", 0, 0, 0);
    }

    public ArrayList<PlayerStats> getAllStats(String uuid) {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        try {
            getStats.setString(1, uuid);
            ResultSet rs = getStats.executeQuery();
            while (rs.next()) {
                //ResultSet automatically converts a null int to 0
                stats.add(new PlayerStats(rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("It appears as if either the DB is offline or this UUID has no attached data.");
            //Drop the stacktrace - this error will occur commonly as new players join and populate the DB.
            //e.printStackTrace();
        }
        return stats;
    }

    public ArrayList<PlayerStats> getAllStats() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        try {
            ResultSet rs = getAllStats.executeQuery();
            while (rs.next()) {
                stats.add(new PlayerStats(rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<GameData> getCurrentData() {
        return archives;
    }

    private List<GameData> getArchives() {
        ArrayList<GameData> data = new ArrayList<GameData>();
        try {
            ResultSet rs = getAllValues.executeQuery();
            // Note: rs.getInt(1) gets the SQL id
            while (rs.next()) {
                data.add(new GameData(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getLastUsedId() {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet rs = getLastId.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ids.size() >= 1) {
            return ids.get(ids.size() - 1);
        } else {
            return GameId.getLastUsedID();
        }
    }

    private class DatabaseKeepAliveTask implements Runnable {
        public void run() {
            Bukkit.getLogger().info("RedditDB // DKAT Refreshed");
            archives = getArchives();
        }
    }

    private class PurgeDuplicateDataTask implements Runnable {
        public void run() {
            if (Bukkit.getOnlinePlayers().size() == 0) {
                Bukkit.getLogger().info("RedditDB // Purging duplicate null data entries");
                ArrayList<String> uuids = new ArrayList<>();
                for (PlayerStats stat : getAllStats()) {
                    if (uuids.contains(stat.getUUID())) {
                        ArrayList<PlayerStats> stats = getAllStats(stat.getUUID());
                        PlayerStats fin = stats.get(0);
                        try {
                            Bukkit.getLogger().info("RedditDB // Found duplicate data with '" + stat.getUUID() + "'");
                            RedditEngine.db.db.createStatement().execute("DELETE FROM RedditStats WHERE uuid = '" + stat.getUUID() + "'");
                            setDefaultData(stat.getUUID());
                            updateStats(stat.getUUID(), fin.getWins(), fin.getLosses(), fin.getKills());
                            Bukkit.getLogger().info("RedditDB // Deleted duplicate data. Recreated original data.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        uuids.add(stat.getUUID());
                    }
                }
            }
        }
    }

}
