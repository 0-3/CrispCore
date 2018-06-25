package network.reborn.core.Util.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLManager implements Runnable {
    private final RebornCore core;
    int attempts = 0;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private ArrayList<MySQLTask> normalTasks = new ArrayList<MySQLTask>();
    private ArrayList<MySQLTask> priorityTasks = new ArrayList<MySQLTask>();
    private boolean shutdown = false;
    private Connection connection = null;

    public MySQLManager(RebornCore core) {
        this.core = core;

        config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://167.114.64.62:3306/live");
        config.setUsername("mc3514");
        config.setPassword("5ad629beb3");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("leakDetectionThreshold", "50");

        dataSource = new HikariDataSource(config);

//		Bukkit.getScheduler().runTaskTimer(core, () -> {
//			attempts = 0;
//			reconnect();
//		}, 15 * (20 * 60), 15 * (20 * 60));
    }

    public void reconnect() {
//		if (attempts > 3) {
//			Bukkit.shutdown();
//			return;
//		}
//		try {
//			this.connection = DriverManager.getConnection("jdbc:mysql://" + config.getHost() + ":3306/" + config.getDatabase() + "?autoReconnect=true", config.getUsername(), config.getPassword());
//		} catch (SQLException e) {
//			attempts++;
//			reconnect();
//			e.printStackTrace();
//		}
    }

    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection = dataSource.getConnection();
            Bukkit.getScheduler().runTaskLaterAsynchronously(core, () -> {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                        connection = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, 20L * 600); // 10 minutes later
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // RIP
    }

    public Connection getNewConnection() {
        try {
            final Connection[] connection = {dataSource.getConnection()};
            Bukkit.getScheduler().runTaskLaterAsynchronously(core, () -> {
                try {
                    if (!connection[0].isClosed()) {
                        connection[0].close();
                        connection[0] = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, 20L * 600); // 10 minutes later
            return connection[0];
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // RIP
    }

    public void scheduleTask(MySQLTask task) {
        normalTasks.add(task);
    }

    public void shutdown() {
        shutdown = true;
    }

    public boolean jobsQueued() {
        return normalTasks.size() > 0 || priorityTasks.size() > 0;
    }

    public void schedulePriorityTask(MySQLTask task) {
        priorityTasks.add(task);
    }

    public void runTaskSynchronously(MySQLTask task) {
        task.run();
    }

    @Override
    public void run() {
        while (true) {
            if (priorityTasks.size() > 0) {
                MySQLTask task = priorityTasks.remove(0);
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (normalTasks.size() > 0) {
                MySQLTask task = normalTasks.remove(0);
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (shutdown && normalTasks.size() == 0 && priorityTasks.size() == 0)
                break;
        }
    }
}
