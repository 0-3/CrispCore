package network.reborn.core.API;

import network.reborn.core.Module.Games.GameState;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Database.MySQLTask;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CoveServer {
    protected Integer players = 0;
    protected Integer maxPlayers = 0;
    protected String song = "";
    protected String gameMap = "";
    protected JSONObject extraData = new JSONObject();
    private int ID = 0;
    private boolean online = false;
    private boolean thisServer = false;
    private Module module = null;
    private String name = null;
    private GameState gameState = null;

    public CoveServer(Integer ID, String name, String IP, int port, Module module, GameState gameState, int players, int maxPlayers) {
        this.ID = ID;
        this.name = name;
        this.module = module;
        this.gameState = gameState;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public CoveServer(String name, String slug, int maxPlayers, boolean thisServer, Module module) {
        this.name = name;
        this.thisServer = thisServer;
        this.module = module;
        if (!thisServer)
            return;

        // TODO Add slug support
        if (RebornCore.getRebornCore().getConfig().isSet("Server ID") || getID() > 0) {
            int serverID = RebornCore.getRebornCore().getConfig().getInt("Server ID");
            if (getID() == 0)
                setID(serverID);
            else
                serverID = getID();

            String serverIP = Bukkit.getIp();
            if (serverIP == null || serverIP.isEmpty() || serverIP.equalsIgnoreCase("")) {
                try {
                    serverIP = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }


            final String sql = "UPDATE `Servers` SET `Name` = '" + name + "', `IP` = '" + serverIP + "', `Port` = '" + RebornCore.getRebornCore().getServer().getPort() + "', `Module` = '" + "TODO" + "', `Players` = " + Bukkit.getOnlinePlayers().size() + ", `MaxPlayers` = " + maxPlayers + ", `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "', `LMCVersion` = '" + RebornCore.getRebornCore().getDescription().getVersion() + "', `Module` = '" + getModule().toString() + "' WHERE `ID` = " + serverID + " ;";
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        } else {
            final String sql = "INSERT INTO `Servers` (`Name`,`IP`,`Port`,`Online`,`Players`,`MaxPlayers`,`LastContact`,`LMCVersion`,`Module`) VALUES ('" + name + "','" + RebornCore.getRebornCore().getServer().getIp() + "','" + RebornCore.getRebornCore().getServer().getPort() + "',0," + Bukkit.getOnlinePlayers().size() + "," + maxPlayers + ",'" + OtherUtil.getCurrentDateTimeToMySQL() + "','" + RebornCore.getRebornCore().getDescription().getVersion() + "','" + getModule().toString() + "');";
            RebornCore.getCoveAPI().getMySQLManager().schedulePriorityTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
                @Override
                public void run() {
                    try {
                        int serverID;
                        serverID = manager.getConnection().createStatement().executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                        ResultSet resultSet = manager.getConnection().createStatement().executeQuery("SELECT LAST_INSERT_ID() AS LastID FROM `Servers`");
                        if (resultSet.next()) serverID = resultSet.getInt("LastID");
                        RebornCore.getRebornCore().getConfig().set("Server ID", serverID);
                        RebornCore.getRebornCore().saveConfig();
                        setID(serverID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (manager.getConnection() != null && !manager.getConnection().isClosed())
                                manager.getConnection().close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                setOnline();
            }
        }, 5 * 20L); // Wait 5 seconds before setting server as "online"
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setOnline() {
        if (thisServer) {
            online = true;
            final String sql = "UPDATE `Servers` SET `Online` = 1, `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + " ;";
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        } else {
            online = true;
        }
    }

    public void setOffline() {
        if (thisServer) {
            online = false;
            final String sql = "UPDATE `Servers` SET `Online` = 0, `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + " ;";
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        } else {
            online = false;
        }
    }

    public boolean isOnline() {
        return online;
    }

    public void syncPlayerCount(boolean isPriority) {
        if (!thisServer)
            return;
        updatePlayerCount(Bukkit.getOnlinePlayers().size(), isPriority);
    }

    public void pushPlayerCount(boolean isPriority) {
        if (!thisServer)
            return;
        String sql = "INSERT INTO `PlayerCounts` (`ServerID`,`Online`,`Max`,`Date`) VALUES (" + getID() + "," + Bukkit.getOnlinePlayers().size() + "," + Bukkit.getMaxPlayers() + ",'" + OtherUtil.getCurrentDateToMySQL() + "')";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public void updatePlayerCount(int count, boolean isPriority) {
        if (!thisServer)
            return;
        final String sql = "UPDATE `Servers` SET `players` = " + count + ", `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + " ;";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public void updateUsedRAM(String ram, boolean isPriority) {
        if (!thisServer)
            return;
        final String sql = "UPDATE `Servers` SET `UsedRAM` = '" + ram + "', `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + " ;";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public void updateTotalRAM(String ram, boolean isPriority) {
        if (!thisServer)
            return;
        final String sql = "UPDATE `Servers` SET `TotalRAM` = '" + ram + "', `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + " ;";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public void updateFreeRAM(String ram, boolean isPriority) {
        if (!thisServer)
            return;
        final String sql = "UPDATE `Servers` SET `FreeRAM` = '" + ram + "', `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + ";";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public void updateTPS(double tps, boolean isPriority) {
        if (!thisServer)
            return;
        final String sql = "UPDATE `Servers` SET `TPS` = '" + tps + "', `LastContact` = '" + OtherUtil.getCurrentDateTimeToMySQL() + "' WHERE `ID` = " + getID() + ";";
        if (isPriority)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        else
            RebornCore.getCoveAPI().runSQLQuery(sql);
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Integer getPlayers() {
        return players;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public JSONObject getExtraData() {
        return extraData;
    }

    public void setExtraData(JSONObject extraData) {
        setExtraData(extraData, true);
    }

    public void setExtraData(JSONObject extraData, boolean updateSQL) {
        String sql = "UPDATE `Servers` SET `Data` = '" + extraData.toJSONString() + "' WHERE `ID` = " + getID() + ";";
        if (updateSQL)
            RebornCore.getCoveAPI().runSQLQueryPriority(sql);
        this.extraData = extraData;
    }
}
