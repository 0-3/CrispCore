package network.reborn.core.API;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import network.reborn.core.Events.ActionBarMessageEvent;
import network.reborn.core.Events.BalanceChangeEvent;
import network.reborn.core.Events.PlayerRunJoinEvent;
import network.reborn.core.Events.TitleSendEvent;
import network.reborn.core.Module.Games.GamePlayer;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Database.MySQLTask;
import network.reborn.core.Util.DefaultFontInfo;
import network.reborn.core.Util.OtherUtil;
import network.reborn.core.Util.ReflectionUtil;
import network.reborn.proxy.API.Notify;
import network.reborn.proxy.API.RebornServer;
import network.reborn.proxy.RebornProxy;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RebornPlayer {
    private long playerID;
    private UUID uuid;
    private String name;
    private ServerRank serverRank = ServerRank.DEFAULT;
    private DonorRank donorRank = DonorRank.DEFAULT;
    private ArrayList<String> permissions = new ArrayList<>();
    private HashMap<String, Integer> currencies = new HashMap<>();
    private int runJoinID = 0;
    private boolean flying = false;
    private boolean afk = false;
    private boolean loadedDB = false;
    private boolean runJoin = false;
    private boolean chatAlerts = true;
    private boolean doubleJump = true;
    private boolean chatEnabled = true;
    private boolean vanished = false;
    private boolean nicked = false;

    public RebornPlayer(UUID uuid) {
        this(uuid, true);
    }

    public RebornPlayer(UUID uuid, boolean async) {
        this.uuid = uuid;
        setup(async);
    }

    private void setup(boolean async) {
        if (loadedDB)
            return;

        if (!async) {
            doSetupStuff();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                doSetupStuff();
            }
        }.runTaskAsynchronously(RebornCore.getRebornCore());
    }

    private void doSetupStuff() {
        Connection connection = RebornCore.getCoveAPI().getMySQLManager().getConnection();
        String sql = "SELECT * FROM `players` WHERE `UUID` = '" + getUUID().toString() + "';";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            if (resultSet.next()) {
                setServerRank(ServerRank.valueOf(resultSet.getString("ServerRank").toUpperCase()));
                setDonorRank(DonorRank.valueOf(resultSet.getString("DonorRank").toUpperCase()));
                playerID = resultSet.getInt("ID");

                Bukkit.getScheduler().runTask(RebornCore.getRebornCore(), () -> {
                    try {
                        if (resultSet.getString("Nick") != null && !resultSet.getString("Nick").isEmpty()) {
                            name = resultSet.getString("Nick");
                            nicked = true;
                        }
                        setNick(resultSet.getString("Nick"), true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                // Load permissions
                sql = "SELECT `Permission` FROM `permissions` WHERE `UUID` = '" + resultSet.getString("UUID") + "';";
                ResultSet resultSet1 = connection.createStatement().executeQuery(sql);
                if (resultSet1.next()) {
                    resultSet1.beforeFirst();
                    while (resultSet1.next()) {
                        permissions.add(resultSet1.getString("Permission"));
                    }
                }

                // Load currencies
                sql = "SELECT `Tag`, `Balance` FROM `currencies` WHERE `UUID` = '" + resultSet.getString("UUID") + "';";
                ResultSet resultSet2 = connection.createStatement().executeQuery(sql);
                if (resultSet2.next()) {
                    resultSet2.beforeFirst();
                    while (resultSet2.next()) {
                        currencies.put(resultSet2.getString("Tag"), resultSet2.getInt("Balance"));
                    }
                }

                if (getBalance("NetworkLevel") < 1)
                    setBalance("NetworkLevel", 1, true); // Make sure player always have a network level of at least 1!

                // Load player settings
                ResultSet playerSettingsResults = connection.createStatement().executeQuery("SELECT `key`, `value` FROM `player_settings` WHERE `UUID` = '" + resultSet.getString("UUID") + "';");
                if (playerSettingsResults.next()) {
                    playerSettingsResults.beforeFirst();
                    while (playerSettingsResults.next()) {
                        switch (playerSettingsResults.getString("key")) {
                            case "chat_alerts":
                                setChatAlerts(playerSettingsResults.getInt("value") == 1, false);
                                break;
                            case "double_jump":
                                setDoubleJump(playerSettingsResults.getInt("value") == 1, false);
                                break;
                            case "chat":
                                setChatEnabled(playerSettingsResults.getInt("value") == 1, false);
                                break;
                        }
                    }
                }

                RebornPlayer rebornPlayer = this;
//				Bukkit.getScheduler().runTask(RebornCore.getRebornCore(), () -> {
//					if (!RebornCore.achievementHandler.getWelcome().alreadyEarned(rebornPlayer))
//						RebornCore.achievementHandler.getWelcome().giveAchievement(rebornPlayer);
//				});

            } else {
                Bukkit.getScheduler().runTask(RebornCore.getRebornCore(), () -> getPlayer().kickPlayer("Data failed to load :( Try re-logging"));
            }
        } catch (SQLException e) {
            // Data failed to load, print stack trace, kick player, reconnect to MySQL
            e.printStackTrace();
            Bukkit.getScheduler().runTask(RebornCore.getRebornCore(), () -> getPlayer().kickPlayer("Data failed to load :( Try re-logging"));
            RebornCore.getCoveAPI().getMySQLManager().reconnect();
        } finally {
            // Setup done
            loadedDB = true;
        }
    }

    public boolean isLoadedDB() {
        return loadedDB;
    }

    public void setLoadedDB(boolean loadedDB) {
        this.loadedDB = loadedDB;
    }

    public void setRunJoin(final boolean runJoin) {
        if (runJoin && this.loadedDB) {
            runJoin();
        } else if (runJoin) {
            final Runnable runnable = () -> {
                if (loadedDB) {
                    runJoin();
                    Bukkit.getScheduler().cancelTask(runJoinID);
                }
            };
            runJoinID = Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), runnable, 5L, 5L).getTaskId();
        }
        this.runJoin = runJoin;
    }

    private void runJoin() {
        if (!isOnline() || !isLoadedDB())
            return;

        PlayerRunJoinEvent playerRunJoinEvent = new PlayerRunJoinEvent(this);
        Bukkit.getPluginManager().callEvent(playerRunJoinEvent);
        if (playerRunJoinEvent.isCanceled())
            return;

        if (RebornCore.getCoveAPI().getModule() instanceof Hub || RebornCore.getCoveAPI().getModule() instanceof SMP) { // Enable on Hub AND SMP
            if (getServerRank() != ServerRank.DEFAULT)
                NametagEdit.getApi().setPrefix(getPlayer(), getServerRank().getTabName());
            else
                NametagEdit.getApi().setPrefix(getPlayer(), getDonorRank().getTabName());
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
                if (getServerRank() != ServerRank.DEFAULT)
                    NametagEdit.getApi().setPrefix(getPlayer(), getServerRank().getTabName());
                else
                    NametagEdit.getApi().setPrefix(getPlayer(), getDonorRank().getTabName());
            }, 20L);
        }

        /*for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getName().equals(playerName)) {
                player.setPlayerListName(RebornAPI.playerListNames.get(player.getName()));
            }
        }*/

        // if admin+ keep player disguised, otherwise undisguise
		/*if (!canPlayer(ServerRank.ADMIN)) {
            if (serverRank != ServerRank.DEFAULT) {
                getPlayer().setPlayerListName(getServerRank().getTabName() + getPlayer().getDisplayName());
                RebornAPI.playerListNames.put(playerName, getServerRank().getTabName() + getPlayer().getDisplayName());
            } else {
                getPlayer().setPlayerListName(getDonorRank().getTabName() + getPlayer().getDisplayName());
                RebornAPI.playerListNames.put(playerName, getDonorRank().getTabName() + getPlayer().getDisplayName());
            }
            DisguiseAPI.undisguiseToAll(getPlayer());
        } else {
            String name = Core.getAPI().getRandomRealisticPlayerName();
            getPlayer().setPlayerListName(name);
            String[] args = new String[2];
            args[0] = "player";
            args[1] = name;
            disguisePlayer(args, true);
        }*/
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        if (this.name == null || this.name.equalsIgnoreCase("")) {
            return getPlayer().getName();
        }
        return this.name;
    }

    public boolean isOnline() {
        Player player = getPlayer();
        return player != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public ServerRank getServerRank() {
        return getServerRank(false);
    }

    public void setServerRank(ServerRank serverRank) {
        this.serverRank = serverRank;
    }

    public ServerRank getServerRank(boolean realOnly) {
        if (realOnly)
            return serverRank;
        return isNicked() ? ServerRank.DEFAULT : serverRank;
    }

    public DonorRank getDonorRank() {
        return donorRank;
    }

    public void setDonorRank(DonorRank donorRank) {
        this.donorRank = donorRank;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void givePermission(String permission) {
        if (hasPermission(permission))
            return;
        permissions.add(permission);
        String sql = "INSERT INTO `permissions` (`UUID`,`Permission`) VALUES ('" + getUUID() + "','" + permission + "');";
        RebornCore.getCoveAPI().runSQLQueryPriority(sql);
    }

    public void removePermission(String permission) {
        if (!hasPermission(permission))
            return;
        permissions.remove(permission);
        String sql = "DELETE FROM `permissions` WHERE `UUID` = '" + getUUID() + "' AND `Permission` = '" + permission + "';";
        RebornCore.getCoveAPI().runSQLQueryPriority(sql);
    }

    public void doNiceTeleport(World world, double x, double y, double z, boolean showMessage) {
        doNiceTeleport(new Location(world, x, y, z), showMessage);
    }

    public void doNiceTeleport(double x, double y, double z, boolean showMessage) {
        doNiceTeleport(new Location(getPlayer().getWorld(), x, y, z), showMessage);
    }

    public void doNiceTeleport(final Location location, final boolean showMessage) {
        doNiceTeleport(location, showMessage, true);
    }

    public void doNiceTeleport(final Location location, final boolean showMessage, final boolean safe) {
        if (!location.getChunk().isLoaded())
            location.getChunk().load();

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!location.getChunk().isLoaded()) {
                    if (showMessage) {
                        sendActionBar(ChatColor.RED + "" + ChatColor.BOLD + "Teleporting...");
                    }
                    return;
                }
                if (safe)
                    location.setY(location.getWorld().getHighestBlockYAt(location) + 2);
                getPlayer().teleport(location);
                this.cancel();
            }
        };
        bukkitRunnable.runTaskTimer(RebornCore.getRebornCore(), 5L, 5L);
    }

    public void sendCentredMessage(String message) {
        if (!isOnline()) return;
        if (message == null || message.equals("")) {
            getPlayer().sendMessage("");
            return;
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int CENTER_PX = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        getPlayer().sendMessage(sb.toString() + message);
    }

    public void sendActionBar(String message) {
        Player player = getPlayer();
        ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (actionBarMessageEvent.isCancelled())
            return;

        String nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            if ((nmsver.equalsIgnoreCase("v1_8_R1") || !nmsver.startsWith("v1_8_")) && !nmsver.startsWith("v1_9_")) {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendActionBar(final String message, int duration) {
        sendActionBar(message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar("");
                }
            }.runTaskLater(RebornCore.getRebornCore(), duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
        while (duration > 60) {
            duration -= 60;
            int sched = duration % 60;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(message);
                }
            }.runTaskLater(RebornCore.getRebornCore(), (long) sched);
        }
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendTitle(String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        Player player = getPlayer();
        TitleSendEvent titleSendEvent = new TitleSendEvent(player, title, subtitle);
        Bukkit.getPluginManager().callEvent(titleSendEvent);
        if (titleSendEvent.isCancelled())
            return;

        try {
            Object e;
            Object chatTitle;
            Object chatSubtitle;
            Constructor subtitleConstructor;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                sendPacket(player, titlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTitle() {
        sendTitle("", "", 0, 0, 0);
    }

    public void sendTabTitle(String header, String footer) {
        if (!isOnline()) return;
        if (header == null) header = "";
        header = ChatColor.translateAlternateColorCodes('&', header + "\n");

        if (footer == null) footer = "";
        footer = ChatColor.translateAlternateColorCodes('&', "\n" + footer);

        try {
            Object tabHeader = ReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
            Object tabFooter = ReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
            Constructor<?> titleConstructor = ReflectionUtil.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent"));
            Object packet = titleConstructor.newInstance(tabHeader);
            Field field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, tabFooter);
            ReflectionUtil.sendPacket(getPlayer(), packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean canPlayer(ServerRank requiredRank) {
        int currentRankID = getServerRank(true).getRankID();
        int requiredRankID = requiredRank.getRankID();

        return currentRankID >= requiredRankID;
    }

    public boolean canPlayer(DonorRank requiredRank) {
        int currentRankID = getDonorRank().getRankID();
        int requiredRankID = requiredRank.getRankID();

        return currentRankID >= requiredRankID;
    }

    public boolean isPlayer(ServerRank requiredRank) {
        int currentRankID = getServerRank().getRankID();
        int requiredRankID = requiredRank.getRankID();

        return currentRankID == requiredRankID;
    }

    public boolean isPlayer(DonorRank requiredRank) {
        int currentRankID = getDonorRank().getRankID();
        int requiredRankID = requiredRank.getRankID();

        return currentRankID == requiredRankID;
    }

    public void disguisePlayer(String[] args) {
        disguisePlayer(args, false, null);
    }

    public void disguisePlayer(String[] args, boolean sendMessageToPlayer) {
        disguisePlayer(args, sendMessageToPlayer, null);
    }

    public void disguisePlayer(String[] args, boolean sendMessageToPlayer, Player whoDidThis) {
        // TODO Send message to player when boolean is true... (Partly done just for player)
        if (!isOnline())
            return;
        PlayerDisguise playerDisguise;
        MobDisguise mobDisguise;
        switch (args[0].toLowerCase().replaceAll("_", "").replaceAll("-", "")) {
            default:
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.RED + "Unknown disguise type (" + args[0] + ")");
                break;
            case "player":
                if (args.length < 2) {
                    if (whoDidThis != null)
                        whoDidThis.sendMessage(ChatColor.RED + "Correct Usage: /disguise player <name>");
                    return;
                }
                if (args.length == 3) {
                    playerDisguise = new PlayerDisguise(args[1], args[2]);
                } else {
                    playerDisguise = new PlayerDisguise(args[1]);
                }
                DisguiseAPI.disguiseToAll(getPlayer(), playerDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Player (" + args[1] + ")");
                if (sendMessageToPlayer && isOnline()) {
                    String msg = "[\"\",{\"text\":\"You have been disguised as a \",\"color\":\"green\"},{\"text\":\"Player\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + args[1] + "\"}]}}}]";
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + getPlayer().getName() + " " + msg);
                }

                break;
            case "zombie":
                mobDisguise = new MobDisguise(DisguiseType.ZOMBIE, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Zombie");
                break;
            case "zombievillager":
                mobDisguise = new MobDisguise(DisguiseType.ZOMBIE_VILLAGER, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Zombie Villager");
                break;
            case "skeleton":
                mobDisguise = new MobDisguise(DisguiseType.SKELETON, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Skeleton");
                break;
            case "spider":
                mobDisguise = new MobDisguise(DisguiseType.SPIDER, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Spider");
                break;
            case "horse":
                mobDisguise = new MobDisguise(DisguiseType.HORSE, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Horse");
                break;
            case "slime":
                mobDisguise = new MobDisguise(DisguiseType.SLIME, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Slime");
                break;
            case "creeper":
            case "creep":
                mobDisguise = new MobDisguise(DisguiseType.CREEPER, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Creeper");
                break;
            case "pigman":
            case "pigmen":
            case "zombiepigmen":
            case "zombiepigman":
                mobDisguise = new MobDisguise(DisguiseType.PIG_ZOMBIE, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Zombie Pigman");
                break;
            //TODO: Enable for 1.9+
			/*case "shulkar":
			case "shulker":
				mobDisguise = new MobDisguise(EntityType.SHULKER, true);
				DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
				if (whoDidThis != null)
					whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Skulker");
				break;*/
            case "cow":
                mobDisguise = new MobDisguise(EntityType.COW, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Cow");
                break;
            case "pig":
                mobDisguise = new MobDisguise(EntityType.PIG, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Pig");
                break;
            case "giant":
                mobDisguise = new MobDisguise(EntityType.GIANT, true);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a Giant");
                break;
            case "dog":
            case "wolf":
                mobDisguise = new MobDisguise(EntityType.WOLF);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as a wolf.");
                break;
            case "ocelot":
            case "cat":
                mobDisguise = new MobDisguise(EntityType.OCELOT);
                DisguiseAPI.disguiseToAll(getPlayer(), mobDisguise);
                if (whoDidThis != null)
                    whoDidThis.sendMessage(ChatColor.GREEN + "Disguised as an ocelot.");
                break;
        }
    }

    public void giveBalance(String tag, int coins) {
        giveBalance(tag, coins, false);
    }

    public void giveBalance(String tag, int coins, boolean useMultipliers) {
        giveBalance(tag, coins, useMultipliers, false);
    }

    public void giveBalance(String tag, int coins, boolean useMultipliers, boolean showMessage) {
        giveBalance(tag, coins, useMultipliers, showMessage, "");
    }

    public void giveBalance(String tag, int amount, boolean useMultipliers, boolean showMessage, String messageText) {
        if (amount <= 0) return;

        int multiplier = 1;
//		if (getDonorRank().equals(DonorRank.CAPTAIN)) {
//			multiplier = 4;
//		} else if (getDonorRank().equals(DonorRank.FIRST_MATE)) {
//			multiplier = 3;
//		} else if (getDonorRank().equals(DonorRank.RIGGER)) {
//			multiplier = 2;
//		}

//		if (Core.coinMultiplier > 1) // TODO
//			multiplier = (multiplier - 1) + Core.coinMultiplier;

//		if (useMultipliers && multiplier > 1)
//			amount = amount * multiplier;

        int newCoins = getBalance(tag) + amount;

        if (RebornCore.getCoveAPI().getGame() != null) {
            GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(getUUID());
            gamePlayer.addCoins(amount);
        }

        if (showMessage) {
            String message = "+" + amount + " " + tag;
            if (!messageText.equals(""))
                message = messageText.replaceAll("%amount%", String.valueOf(amount));
//			if (!Core.coinReason.equals("") && useMultipliers) // TODO
//				message = message + " (" + Core.coinReason + ")";
            final String finalMessage = message;
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
                @Override
                public void run() {
                    getPlayer().sendMessage(finalMessage);
                }
            }, 2L); // Delay so it appears below other messages
        }

        setBalance(tag, newCoins);
    }

    public void takeBalance(String tag, int coins) {
        takeBalance(tag, coins, false, "");
    }

    public void takeBalance(String tag, int coins, boolean showMessage, String messageText) {
        if (coins <= 0) return;
        int newCoins = getBalance(tag) - coins;
        if (newCoins < 0)
            newCoins = 0;

        if (showMessage) {
            String message = "-" + coins + " " + tag;
            if (!messageText.equals(""))
                message = messageText.replaceAll("%amount%", String.valueOf(coins));
            final String finalMessage = message;
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
                @Override
                public void run() {
                    getPlayer().sendMessage(finalMessage);
                }
            }, 2L); // Delay so it appears below other messages
        }

        setBalance(tag, newCoins);
    }

    public void setBalance(final String tag, int coins) {
        setBalance(tag, coins, false);
    }

    public void setBalance(final String tag, int coins, boolean forceDBUpdate) {
        if (!forceDBUpdate && (coins < 0 || (currencies.containsKey(tag) && currencies.get(tag) == coins))) return;

        BalanceChangeEvent balanceChangeEvent = new BalanceChangeEvent(getPlayer(), tag, getBalance(tag), coins);
        Bukkit.getPluginManager().callEvent(balanceChangeEvent);

        currencies.put(tag, balanceChangeEvent.getNewBalance());
        final int coinsBalance = balanceChangeEvent.getNewBalance();
        RebornCore.getCoveAPI().getMySQLManager().schedulePriorityTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                if (getUUID() == null)
                    return;

                try {
                    String sql = "SELECT `UUID` FROM `currencies` WHERE `Tag` = '" + tag + "' AND `UUID` = '" + getUUID() + "';";
                    ResultSet resultSet = manager.getConnection().createStatement().executeQuery(sql);
                    if (resultSet.next()) {
                        // Update currency
                        sql = "UPDATE `currencies` SET `Balance` = " + coinsBalance + " WHERE `UUID` = '" + getUUID() + "' AND `Tag` = '" + tag + "';";
                    } else {
                        // Insert currency
                        sql = "INSERT INTO `currencies` (`Tag`,`Balance`,`UUID`) VALUES ('" + tag + "'," + coinsBalance + ",'" + getUUID() + "');";
                    }
                    manager.getConnection().createStatement().execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getBalance(String tag) {
        if (!currencies.containsKey(tag))
            return 0;
        return currencies.get(tag);
    }

    public boolean isNotify(Notify Type) {
        return RebornProxy.getRebornAPI().getCovePlayer(getUUID()).getNotify(Type);
    }

    public void setNotify(Notify Type, Boolean State) {
        RebornProxy.getRebornAPI().getCovePlayer(getUUID()).setNotify(Type, State);
    }

    public void addStat(String tableName, final StatType statType, final int amount, final String other) {
        final String table = "Stats_" + tableName;
        final String query = "CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                "`UUID` varchar(50) NOT NULL PRIMARY KEY," +
                "`Plays` int(11) NOT NULL," +
                "`Kills` int(11) NOT NULL," +
                "`Deaths` int(11) NOT NULL," +
                "`Wins` int(11) NOT NULL" +
                ") ";
        RebornCore.getCoveAPI().getMySQLManager().scheduleTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                if (other == null || other.isEmpty()) {
                    try {
                        manager.getConnection().createStatement().execute(query);

                        String sql = "SELECT `UUID` FROM `" + table + "` WHERE `UUID` = '" + getUUID().toString() + "';";
                        if (manager.getConnection().createStatement().executeQuery(sql).next()) {
                            String type = "";
                            switch (statType) {
                                case PLAY:
                                    type = "Plays";
                                    break;
                                case KILL:
                                    type = "Kills";
                                    break;
                                case DEATH:
                                    type = "Deaths";
                                    break;
                                case WIN:
                                    type = "Wins";
                                    break;
                            }
                            sql = "UPDATE `" + table + "` SET `" + type + "` = `" + type + "` + " + amount + " WHERE `UUID` = '" + getUUID().toString() + "';";
                        } else {
                            int plays = 0;
                            int kills = 0;
                            int deaths = 0;
                            int wins = 0;
                            switch (statType) {
                                case PLAY:
                                    plays = amount;
                                    break;
                                case KILL:
                                    kills = amount;
                                    break;
                                case DEATH:
                                    deaths = amount;
                                    break;
                                case WIN:
                                    wins = amount;
                                    break;
                            }
                            sql = "INSERT INTO `" + table + "` (`UUID`,`Plays`,`Kills`,`Deaths`,`Wins`) VALUES ('" + getUUID().toString() + "', " + plays + ", " + kills + ", " + deaths + ", " + wins + ");";
                        }
                        manager.getConnection().createStatement().execute(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    String sql = "ALTER TABLE " + table + " ADD " + other + " INT(11) NOT NULL DEFAULT 0;";
                    try {
                        manager.getConnection().createStatement().execute(sql);
                        sql = "SELECT `UUID` FROM `" + table + "` WHERE `UUID` = '" + getUUID().toString() + "';";
                        if (!manager.getConnection().createStatement().executeQuery(sql).next()) {
                            sql = "INSERT INTO `" + table + "` (`UUID`,`Plays`,`Kills`,`Deaths`,`Wins`) VALUES ('" + getUUID().toString() + "', 0, 0, 0, 0);";
                            manager.getConnection().createStatement().execute(sql);
                        }
                        sql = "UPDATE `" + table + "` SET `" + other + "` = `" + other + "` + " + amount + " WHERE `UUID` = '" + getUUID().toString() + "';";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendToRandomHub() {
        ArrayList<CoveServer> servers = RebornCore.getServers(network.reborn.core.API.Module.HUB);
        int id = servers.get(OtherUtil.randInt(0, servers.size() - 1)).getID();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(String.valueOf(id));
        getPlayer().sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out.toByteArray());
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
    }

    public long getPlayerID() {
        return playerID;
    }

    public void setChatAlerts(boolean chatAlerts, boolean updateSQL) {
        this.chatAlerts = chatAlerts;
        if (!updateSQL)
            return;
        RebornCore.getCoveAPI().getMySQLManager().schedulePriorityTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                String query = "SELECT * FROM `player_settings` WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'chat_alerts';";
                ResultSet resultSet;
                try {
                    resultSet = manager.getConnection().createStatement().executeQuery(query);
                    if (resultSet.next()) {
                        // Update
                        query = "UPDATE `player_settings` SET `value` = " + (chatAlerts ? 1 : 0) + " WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'chat_alerts'";
                    } else {
                        // Insert
                        query = "INSERT INTO `player_settings` (`UUID`, `key`, `value`) VALUES ('" + getUUID().toString() + "', 'chat_alerts', " + (chatAlerts ? 1 : 0) + ");";
                    }
                    manager.getConnection().createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isDoubleJump() {
        return doubleJump;
    }

    public void setDoubleJump(boolean doubleJump) {
        setDoubleJump(doubleJump, true);
    }

    public void setDoubleJump(boolean doubleJump, boolean updateSQL) {
        this.doubleJump = doubleJump;
        if (!updateSQL)
            return;
        RebornCore.getCoveAPI().getMySQLManager().schedulePriorityTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                String query = "SELECT * FROM `player_settings` WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'double_jump';";
                ResultSet resultSet;
                try {
                    resultSet = manager.getConnection().createStatement().executeQuery(query);
                    if (resultSet.next()) {
                        // Update
                        query = "UPDATE `player_settings` SET `value` = " + (doubleJump ? 1 : 0) + " WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'double_jump'";
                    } else {
                        // Insert
                        query = "INSERT INTO `player_settings` (`UUID`, `key`, `value`) VALUES ('" + getUUID().toString() + "', 'double_jump', " + (doubleJump ? 1 : 0) + ");";
                    }
                    manager.getConnection().createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isChatAlerts() {
        return chatAlerts;
    }

    public void setChatAlerts(boolean chatAlerts) {
        setChatAlerts(chatAlerts, true);
    }

    public void setChatEnabled(boolean chatEnabled, boolean updateSQL) {
        this.chatEnabled = chatEnabled;
        if (!updateSQL)
            return;
        RebornCore.getCoveAPI().getMySQLManager().schedulePriorityTask(new MySQLTask(RebornCore.getCoveAPI().getMySQLManager()) {
            @Override
            public void run() {
                String query = "SELECT * FROM `player_settings` WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'chat';";
                ResultSet resultSet;
                try {
                    resultSet = manager.getConnection().createStatement().executeQuery(query);
                    if (resultSet.next()) {
                        // Update
                        query = "UPDATE `player_settings` SET `value` = " + (chatEnabled ? 1 : 0) + " WHERE `UUID` = '" + getUUID().toString() + "' AND `key` = 'chat'";
                    } else {
                        // Insert
                        query = "INSERT INTO `player_settings` (`UUID`, `key`, `value`) VALUES ('" + getUUID().toString() + "', 'chat', " + (chatEnabled ? 1 : 0) + ");";
                    }
                    manager.getConnection().createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        setChatEnabled(chatEnabled, true);
    }

    public Inventory getMyProfileGUI() {
        Inventory inventory = Bukkit.createInventory(null, 27, "My Profile");

        ItemStack settingsItem = new ItemStack(Material.DAYLIGHT_DETECTOR);
        ItemMeta settingsItemMeta = settingsItem.getItemMeta();
        settingsItemMeta.setDisplayName(ChatColor.GREEN + "My Settings");
        settingsItem.setItemMeta(settingsItemMeta);
        inventory.setItem(10, settingsItem);

        ItemStack playerInfoItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta playerInfoItemMeta = (SkullMeta) playerInfoItem.getItemMeta();
        playerInfoItemMeta.setOwner(getPlayer().getName());
        playerInfoItemMeta.setDisplayName(ChatColor.GREEN + "Player Information");

        List<String> lore = new ArrayList<>();

        String rank;
        if (getServerRank() != ServerRank.DEFAULT)
            rank = getServerRank().getNiceName(true);
        else
            rank = getDonorRank().getNiceName(true);

        int xpTillNextLevel = 10000;
        xpTillNextLevel = xpTillNextLevel - getBalance("NetworkXP");

        lore.add("");
        lore.add(ChatColor.GRAY + "Name: " + ChatColor.YELLOW + getPlayer().getName());
        lore.add(ChatColor.GRAY + "Rank: " + rank);
        lore.add(ChatColor.GRAY + "Gold: " + ChatColor.GOLD + getBalance("Gold"));
        lore.add(ChatColor.GRAY + "Network Level: " + ChatColor.AQUA + (getBalance("NetworkLevel") > 0 ? getBalance("NetworkLevel") : 1));
        lore.add(ChatColor.GRAY + "XP till Level " + (getBalance("NetworkLevel") + 1) + ": " + ChatColor.GREEN + xpTillNextLevel);
        lore.add(ChatColor.GRAY + "Player No: " + ChatColor.AQUA + OtherUtil.ordinal((int) getPlayerID()));
        lore.add("");
        playerInfoItemMeta.setLore(lore);

        playerInfoItem.setItemMeta(playerInfoItemMeta);
        inventory.setItem(12, playerInfoItem);

        ItemStack achievementsItem = new ItemStack(Material.DIAMOND);
        ItemMeta achievementsItemMeta = achievementsItem.getItemMeta();
        achievementsItemMeta.setDisplayName(ChatColor.RED + "Achievements" + ChatColor.GRAY + " (Coming Soon)");
        achievementsItem.setItemMeta(achievementsItemMeta);
        inventory.setItem(14, achievementsItem);

        ItemStack boostersItem = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta boostersItemMeta = boostersItem.getItemMeta();
        boostersItemMeta.setDisplayName(ChatColor.RED + "Boosters" + ChatColor.GRAY + " (Coming Soon)");
        boostersItem.setItemMeta(boostersItemMeta);
        inventory.setItem(16, boostersItem);

        return inventory;
    }

    public Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(null, 45, "My Settings");

        // Chat Notifications
        ItemStack chatAlerts = new ItemStack(Material.JUKEBOX);
        ItemMeta chatAlertsMeta = chatAlerts.getItemMeta();
        chatAlertsMeta.setDisplayName((isChatAlerts() ? ChatColor.GREEN : ChatColor.RED) + "Chat Notifications");
        chatAlertsMeta.setLore(OtherUtil.stringToLore("Toggles a ping sound when you are mentioned by another player in chat.", ChatColor.GRAY));
        chatAlerts.setItemMeta(chatAlertsMeta);
        inventory.setItem(10, chatAlerts);

        ItemStack chatAlertsToggle = new ItemStack(Material.INK_SACK, 1, (short) (isChatAlerts() ? 10 : 8));
        ItemMeta chatAlertsToggleMeta = chatAlertsToggle.getItemMeta();
        chatAlertsToggleMeta.setDisplayName((isChatAlerts() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        chatAlertsToggleMeta.setLore(OtherUtil.stringToLore((isChatAlerts() ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        chatAlertsToggle.setItemMeta(chatAlertsToggleMeta);
        inventory.setItem(19, chatAlertsToggle);


        // Hub double jump
        ItemStack doubleJump = new ItemStack(Material.FEATHER);
        ItemMeta doubleJumpMeta = doubleJump.getItemMeta();
        doubleJumpMeta.setDisplayName((isDoubleJump() ? ChatColor.GREEN : ChatColor.RED) + "Double Jump");
        doubleJumpMeta.setLore(OtherUtil.stringToLore("Toggles being able to double jump in hubs/lobbies.", ChatColor.GRAY));
        doubleJump.setItemMeta(doubleJumpMeta);
        inventory.setItem(12, doubleJump);

        ItemStack doubleJumpToggle = new ItemStack(Material.INK_SACK, 1, (short) (isDoubleJump() ? 10 : 8));
        ItemMeta doubleJumpToggleMeta = doubleJumpToggle.getItemMeta();
        doubleJumpToggleMeta.setDisplayName((isDoubleJump() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        doubleJumpToggleMeta.setLore(OtherUtil.stringToLore((isDoubleJump() ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        doubleJumpToggle.setItemMeta(doubleJumpToggleMeta);
        inventory.setItem(21, doubleJumpToggle);


        // Chat Enabled
        ItemStack chatEnabled = new ItemStack(Material.PAPER);
        ItemMeta chatEnabledMeta = chatEnabled.getItemMeta();
        chatEnabledMeta.setDisplayName((isChatEnabled() ? ChatColor.GREEN : ChatColor.RED) + "Chat Enabled");
        chatEnabledMeta.setLore(OtherUtil.stringToLore("Toggles ability to see and use chat.", ChatColor.GRAY));
        chatEnabled.setItemMeta(chatEnabledMeta);
        inventory.setItem(14, chatEnabled);

        ItemStack chatEnabledToggle = new ItemStack(Material.INK_SACK, 1, (short) (isChatEnabled() ? 10 : 8));
        ItemMeta chatEnabledToggleMeta = chatEnabledToggle.getItemMeta();
        chatEnabledToggleMeta.setDisplayName((isChatEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        chatEnabledToggleMeta.setLore(OtherUtil.stringToLore((isChatEnabled() ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        chatEnabledToggle.setItemMeta(chatEnabledToggleMeta);
        inventory.setItem(23, chatEnabledToggle);


        // Back item
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);

        // Staff Settings
        ItemStack staffSettings = new ItemStack(Material.REDSTONE);
        ItemMeta staffSettingsMeta = staffSettings.getItemMeta();
        staffSettingsMeta.setDisplayName(ChatColor.YELLOW + "Staff");
        staffSettings.setItemMeta(staffSettingsMeta);

        if (canPlayer(ServerRank.HELPER)) {
            inventory.setItem(39, back);
            inventory.setItem(41, staffSettings);
        } else {
            inventory.setItem(40, back);
        }

        return inventory;
    }

    public Inventory getStaffSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(null, 45, "Staff Settings");

        // Report Alerts
        ItemStack reportAlerts = new ItemStack(Material.BOOK);
        ItemMeta reportAlertsMeta = reportAlerts.getItemMeta();
        reportAlertsMeta.setDisplayName((isNotify(Notify.REPORT) ? ChatColor.GREEN : ChatColor.RED) + "Report Alerts");
        reportAlertsMeta.setLore(OtherUtil.stringToLore("Toggles report alerts.", ChatColor.GRAY));
        reportAlerts.setItemMeta(reportAlertsMeta);

        ItemStack reportAlertsToggle = new ItemStack(Material.INK_SACK, 1, (short) (RebornProxy.getRebornAPI().getCovePlayer(getUUID()).getNotify(Notify.REPORT) ? 10 : 8));
        ItemMeta reportAlertsToggleMeta = reportAlertsToggle.getItemMeta();
        reportAlertsToggleMeta.setDisplayName((isNotify(Notify.REPORT) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        reportAlertsToggleMeta.setLore(OtherUtil.stringToLore((isNotify(Notify.REPORT) ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        reportAlertsToggle.setItemMeta(reportAlertsToggleMeta);


        // Cheat Alerts
        ItemStack cheatAlerts = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta cheatAlertsMeta = cheatAlerts.getItemMeta();
        cheatAlertsMeta.setDisplayName((isNotify(Notify.CHEAT) ? ChatColor.GREEN : ChatColor.RED) + "Cheat Alerts");
        cheatAlertsMeta.setLore(OtherUtil.stringToLore("Toggles cheat alerts.", ChatColor.GRAY));
        cheatAlerts.setItemMeta(cheatAlertsMeta);

        ItemStack cheatAlertsToggle = new ItemStack(Material.INK_SACK, 1, (short) (isNotify(Notify.CHEAT) ? 10 : 8));
        ItemMeta cheatAlertsToggleMeta = cheatAlertsToggle.getItemMeta();
        cheatAlertsToggleMeta.setDisplayName((isNotify(Notify.CHEAT) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        cheatAlertsToggleMeta.setLore(OtherUtil.stringToLore((isNotify(Notify.CHEAT) ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        cheatAlertsToggle.setItemMeta(cheatAlertsToggleMeta);


        // Staff Chat
        ItemStack staffChat = new ItemStack(Material.PAPER);
        ItemMeta staffChatMeta = staffChat.getItemMeta();
        staffChatMeta.setDisplayName((isNotify(Notify.STAFF_CHAT) ? ChatColor.GREEN : ChatColor.RED) + "Staff Chat");
        staffChatMeta.setLore(OtherUtil.stringToLore("Toggles ability to see staff chat.", ChatColor.GRAY));
        staffChat.setItemMeta(staffChatMeta);

        ItemStack staffChatToggle = new ItemStack(Material.INK_SACK, 1, (short) (isNotify(Notify.STAFF_CHAT) ? 10 : 8));
        ItemMeta staffChatToggleMeta = staffChatToggle.getItemMeta();
        staffChatToggleMeta.setDisplayName((isNotify(Notify.STAFF_CHAT) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        staffChatToggleMeta.setLore(OtherUtil.stringToLore((isNotify(Notify.STAFF_CHAT) ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        staffChatToggle.setItemMeta(staffChatToggleMeta);

        // Social Spy
        ItemStack socialSpy = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta socialSpyMeta = socialSpy.getItemMeta();
        socialSpyMeta.setDisplayName((isNotify(Notify.SOCIAL_SPY) ? ChatColor.GREEN : ChatColor.RED) + "Social Spy");
        socialSpyMeta.setLore(OtherUtil.stringToLore("Toggles ability to see players messages.", ChatColor.GRAY));
        socialSpy.setItemMeta(socialSpyMeta);

        ItemStack socialSpyToggle = new ItemStack(Material.INK_SACK, 1, (short) (isNotify(Notify.STAFF_CHAT) ? 10 : 8));
        ItemMeta socialSpyToggleMeta = socialSpyToggle.getItemMeta();
        socialSpyToggleMeta.setDisplayName((isNotify(Notify.SOCIAL_SPY) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        socialSpyToggleMeta.setLore(OtherUtil.stringToLore((isNotify(Notify.STAFF_CHAT) ? "Click to Disable" : "Click to Enable"), ChatColor.GRAY));
        socialSpyToggle.setItemMeta(socialSpyToggleMeta);

        if (canPlayer(ServerRank.MODERATOR)) {
            inventory.setItem(10, reportAlerts);
            inventory.setItem(19, reportAlertsToggle);
            inventory.setItem(12, cheatAlerts);
            inventory.setItem(21, cheatAlertsToggle);
            inventory.setItem(14, staffChat);
            inventory.setItem(23, staffChatToggle);
            inventory.setItem(16, socialSpy);
            inventory.setItem(25, socialSpyToggle);
        } else if (isPlayer(ServerRank.HELPER)) {
            inventory.setItem(11, reportAlerts);
            inventory.setItem(20, reportAlertsToggle);
            inventory.setItem(13, cheatAlerts);
            inventory.setItem(22, cheatAlertsToggle);
            inventory.setItem(15, staffChat);
            inventory.setItem(24, staffChatToggle);
        }

        // Back item
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inventory.setItem(40, back);

        return inventory;
    }

    public Inventory getServersGUI() {
        int size = RebornProxy.servers.size();
        if (size % 9 != 0) {
            size = ((size / 9) + 1) * 9;
        }
        Inventory inventory = Bukkit.createInventory(null, size, "Servers");

        int i = 0;
        for (Map.Entry<Integer, RebornServer> entry : RebornProxy.servers.entrySet()) {
            String name;
            if (entry.getValue().isOnline())
                name = ChatColor.GREEN + entry.getValue().getName() + " (" + entry.getValue().getID() + ")" + ChatColor.GRAY + ", ";
            else
                name = ChatColor.RED + entry.getValue().getName() + " (" + entry.getValue().getID() + ")" + ChatColor.GRAY + ", ";

            //TODO ADD ITEM CHANGE BASED ON SERVER TYPE
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(name);
            item.setItemMeta(itemMeta);
            inventory.setItem(i, item);
            i++;

        }
        return inventory;
    }

    public Inventory setRankGUI(String player) {
        if (isPlayer(ServerRank.DEVELOPER) || isPlayer(ServerRank.OWNER)) {
            Inventory inventory = Bukkit.createInventory(null, 36, "Rank > " + player);

            // SERVER RANKS

            // DEFAULT
            ItemStack defaultSRank = new ItemStack(Material.WOOL, 1, DyeColor.SILVER.getData());
            ItemMeta defaultSRankMeta = defaultSRank.getItemMeta();
            defaultSRankMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "DEFAULT");
            defaultSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank DEFAULT", ChatColor.GRAY));
            defaultSRank.setItemMeta(defaultSRankMeta);
            inventory.setItem(2, defaultSRank);

            // MEDIA
            ItemStack mediaSRank = new ItemStack(Material.WOOL, 1, DyeColor.PINK.getData());
            ItemMeta mediaSRankMeta = mediaSRank.getItemMeta();
            mediaSRankMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "MEDIA");
            mediaSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank MEDIA", ChatColor.GRAY));
            mediaSRank.setItemMeta(mediaSRankMeta);
            inventory.setItem(11, mediaSRank);

            // HELPER
            ItemStack helperSRank = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData());
            ItemMeta helperSRankMeta = helperSRank.getItemMeta();
            helperSRankMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "HELPER");
            helperSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank HELPER", ChatColor.GRAY));
            helperSRank.setItemMeta(helperSRankMeta);
            inventory.setItem(4, helperSRank);

            // MODERATOR
            ItemStack modSRank = new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData());
            ItemMeta modSRankMeta = modSRank.getItemMeta();
            modSRankMeta.setDisplayName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "MODERATOR");
            modSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank MODERATOR", ChatColor.GRAY));
            modSRank.setItemMeta(modSRankMeta);
            inventory.setItem(13, modSRank);

            // SENIOR
            ItemStack seniorSRank = new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData());
            ItemMeta seniorSRankMeta = seniorSRank.getItemMeta();
            seniorSRankMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SENIOR");
            seniorSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank SENIOR", ChatColor.GRAY));
            seniorSRank.setItemMeta(seniorSRankMeta);
            inventory.setItem(6, seniorSRank);

            // ADMIN
            ItemStack adminSRank = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
            ItemMeta adminSRankMeta = adminSRank.getItemMeta();
            adminSRankMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "ADMIN");
            adminSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank ADMIN", ChatColor.GRAY));
            adminSRank.setItemMeta(adminSRankMeta);
            inventory.setItem(15, adminSRank);


            // SET GLASS PANES

            // SEPERATOR
            ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData());
            ItemMeta glassPaneMeta = glassPane.getItemMeta();
            glassPaneMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.MAGIC + "SEPERATOR");
            glassPane.setItemMeta(glassPaneMeta);
            inventory.setItem(18, glassPane);
            inventory.setItem(19, glassPane);
            inventory.setItem(20, glassPane);
            inventory.setItem(21, glassPane);
            inventory.setItem(22, glassPane);
            inventory.setItem(23, glassPane);
            inventory.setItem(24, glassPane);
            inventory.setItem(25, glassPane);
            inventory.setItem(26, glassPane);


            // DONOR RANKS

            // DEFAULT
            ItemStack defaultDRank = new ItemStack(Material.WOOL, 1, DyeColor.SILVER.getData());
            ItemMeta defaultDRankMeta = defaultDRank.getItemMeta();
            defaultDRankMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "DEFAULT");
            defaultDRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to donor rank DEFAULT", ChatColor.GRAY));
            defaultDRank.setItemMeta(defaultDRankMeta);
            inventory.setItem(28, defaultDRank);

            // VIP
            ItemStack vipDRank = new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData());
            ItemMeta vipDRankMeta = vipDRank.getItemMeta();
            vipDRankMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP");
            vipDRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to donor rank VIP", ChatColor.GRAY));
            vipDRank.setItemMeta(vipDRankMeta);
            inventory.setItem(30, vipDRank);

            // VIPPLUS
            ItemStack vipplusDRank = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData());
            ItemMeta vipplusDRankMeta = vipplusDRank.getItemMeta();
            vipplusDRankMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "VIP+");
            vipplusDRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to donor rank VIP+", ChatColor.GRAY));
            vipplusDRank.setItemMeta(vipplusDRankMeta);
            inventory.setItem(32, vipplusDRank);

            // REBORN
            ItemStack rebornDRank = new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData());
            ItemMeta rebornDRankMeta = rebornDRank.getItemMeta();
            rebornDRankMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "REBORN");
            rebornDRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to donor rank REBORN", ChatColor.GRAY));
            rebornDRank.setItemMeta(rebornDRankMeta);
            inventory.setItem(34, rebornDRank);

            return inventory;
        } else if (isPlayer(ServerRank.ADMIN)) {
            Inventory inventory = Bukkit.createInventory(null, 9, "Rank > " + player);

            // SERVER RANKS

            // DEFAULT
            ItemStack defaultSRank = new ItemStack(Material.WOOL, 1, DyeColor.SILVER.getData());
            ItemMeta defaultSRankMeta = defaultSRank.getItemMeta();
            defaultSRankMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "DEFAULT");
            defaultSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank DEFAULT", ChatColor.GRAY));
            defaultSRank.setItemMeta(defaultSRankMeta);
            inventory.setItem(1, defaultSRank);

            // MEDIA
            ItemStack mediaSRank = new ItemStack(Material.WOOL, 1, DyeColor.PINK.getData());
            ItemMeta mediaSRankMeta = mediaSRank.getItemMeta();
            mediaSRankMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "MEDIA");
            mediaSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank MEDIA", ChatColor.GRAY));
            mediaSRank.setItemMeta(mediaSRankMeta);
            inventory.setItem(3, mediaSRank);

            // HELPER
            ItemStack helperSRank = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData());
            ItemMeta helperSRankMeta = helperSRank.getItemMeta();
            helperSRankMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "HELPER");
            helperSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank HELPER", ChatColor.GRAY));
            helperSRank.setItemMeta(helperSRankMeta);
            inventory.setItem(5, helperSRank);

            // MODERATOR
            ItemStack modSRank = new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData());
            ItemMeta modSRankMeta = modSRank.getItemMeta();
            modSRankMeta.setDisplayName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "MODERATOR");
            modSRankMeta.setLore(OtherUtil.stringToLore("Set " + player + " to server rank MODERATOR", ChatColor.GRAY));
            modSRank.setItemMeta(modSRankMeta);
            inventory.setItem(7, modSRank);

            return inventory;
        } else {
            return getMyProfileGUI();
        }

    }

    public Inventory getReportGUI(String player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Report > " + player);

        // COMBAT

        // SEV 3
        ItemStack combat3 = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta combat3ItemMeta = combat3.getItemMeta();
        combat3ItemMeta.setDisplayName(ChatColor.RED + "Kill Aura/Forcefield");
        combat3ItemMeta.setLore(OtherUtil.stringToLore("The player snaps onto any other players or entities within a certain radius.", org.bukkit.ChatColor.GRAY));
        combat3.setItemMeta(combat3ItemMeta);
        inventory.setItem(0, combat3);

        // SEV 2
        ItemStack combat2 = new ItemStack(Material.GOLD_SWORD);
        ItemMeta combat2ItemMeta = combat2.getItemMeta();
        combat2ItemMeta.setDisplayName(ChatColor.RED + "Aimbot/Trigger Bot");
        combat2ItemMeta.setLore(OtherUtil.stringToLore("The player locks onto or attacks other entities within their field of view.", org.bukkit.ChatColor.GRAY));
        combat2.setItemMeta(combat2ItemMeta);
        inventory.setItem(9, combat2);

        // SEV 1
        ItemStack combat1 = new ItemStack(Material.IRON_SWORD);
        ItemMeta combat1ItemMeta = combat1.getItemMeta();
        combat1ItemMeta.setDisplayName(ChatColor.RED + "Smooth Aim/Auto Aim");
        combat1ItemMeta.setLore(OtherUtil.stringToLore("The player's crosshair moves to other players slowly to look more realistic.", org.bukkit.ChatColor.GRAY));
        combat1.setItemMeta(combat1ItemMeta);
        inventory.setItem(18, combat1);

        // MINING

        // SEV 3
        ItemStack mining3 = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta mining3ItemMeta = mining3.getItemMeta();
        mining3ItemMeta.setDisplayName(ChatColor.RED + "Xray/Cave Finder");
        mining3ItemMeta.setLore(OtherUtil.stringToLore("The player can see other entities or preferred blocks through walls.", org.bukkit.ChatColor.GRAY));
        mining3.setItemMeta(mining3ItemMeta);
        inventory.setItem(2, mining3);

        // SEV 2
        ItemStack mining2 = new ItemStack(Material.GOLD_PICKAXE);
        ItemMeta mining2ItemMeta = mining2.getItemMeta();
        mining2ItemMeta.setDisplayName(ChatColor.RED + "Illegal Mining");
        mining2ItemMeta.setLore(OtherUtil.stringToLore("The player is mining in a way that is not allowed in the current match.", org.bukkit.ChatColor.GRAY));
        mining2.setItemMeta(mining2ItemMeta);
        inventory.setItem(11, mining2);

        // SEV 1
        ItemStack mining1 = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta mining1ItemMeta = mining1.getItemMeta();
        mining1ItemMeta.setDisplayName(ChatColor.RED + "Trapping/Towering/Camping");
        mining1ItemMeta.setLore(OtherUtil.stringToLore("The player is mining traps or building towers to camp in.", org.bukkit.ChatColor.GRAY));
        mining1.setItemMeta(mining1ItemMeta);
        inventory.setItem(20, mining1);

        // SCRIPTS

        // BOW AIMBOT
        ItemStack bowaim = new ItemStack(Material.BOW);
        ItemMeta bowaimItemMeta = bowaim.getItemMeta();
        bowaimItemMeta.setDisplayName(ChatColor.RED + "Bow Aimbot/Bow Assistance/Fast bow");
        bowaimItemMeta.setLore(OtherUtil.stringToLore("The players bow locks onto targets and may predict movement. "
                + "Bow pulls back at a faster rate/auto pulls back after each shot to hit targets faster.", org.bukkit.ChatColor.GRAY));
        bowaim.setItemMeta(bowaimItemMeta);
        inventory.setItem(4, bowaim);

        // MACRO
        ItemStack macro = new ItemStack(Material.FISHING_ROD);
        ItemMeta macroItemMeta = macro.getItemMeta();
        macroItemMeta.setDisplayName(ChatColor.RED + "Macro");
        macroItemMeta.setLore(OtherUtil.stringToLore("The player uses scripts or external assistance to complete tasks for them.", org.bukkit.ChatColor.GRAY));
        macro.setItemMeta(macroItemMeta);
        inventory.setItem(13, macro);

        // TRACERS
        ItemStack tracer = new ItemStack(Material.COMPASS);
        ItemMeta tracerItemMeta = tracer.getItemMeta();
        tracerItemMeta.setDisplayName(ChatColor.RED + "Tracers/ESP/Wallhack");
        tracerItemMeta.setLore(OtherUtil.stringToLore("The player can locate other players regardless of distance,"
                + " through walls or if the player is shifting.", org.bukkit.ChatColor.GRAY));
        tracer.setItemMeta(tracerItemMeta);
        inventory.setItem(22, tracer);

        // MOVEMENT

        // FLIGHT
        ItemStack fly = new ItemStack(Material.FEATHER);
        ItemMeta flyItemMeta = fly.getItemMeta();
        flyItemMeta.setDisplayName(ChatColor.RED + "Fly/Jesus");
        flyItemMeta.setLore(OtherUtil.stringToLore("The player can fly or appears to walk on water or air.", org.bukkit.ChatColor.GRAY));
        fly.setItemMeta(flyItemMeta);
        inventory.setItem(6, fly);

        // SPEED
        ItemStack speed = new ItemStack(Material.SUGAR);
        ItemMeta speedItemMeta = speed.getItemMeta();
        speedItemMeta.setDisplayName(ChatColor.RED + "Speed/Bunny Hop");
        speedItemMeta.setLore(OtherUtil.stringToLore("The player runs faster that allowed or appears to walk or move over air.", org.bukkit.ChatColor.GRAY));
        speed.setItemMeta(speedItemMeta);
        inventory.setItem(15, speed);

        // NOSLOW
        ItemStack noslow = new ItemStack(Material.FENCE);
        ItemMeta noslowItemMeta = noslow.getItemMeta();
        noslowItemMeta.setDisplayName(ChatColor.RED + "No Slowdown/Velocity");
        noslowItemMeta.setLore(OtherUtil.stringToLore("The player continues full speed while performing slow tasks "
                + "such as drawing back a bow or eating. They may also take a reduced or edited velocity.", org.bukkit.ChatColor.GRAY));
        noslow.setItemMeta(noslowItemMeta);
        inventory.setItem(24, noslow);

        // OTHER ?

        // SEV 3
        ItemStack other3 = new ItemStack(Material.TNT);
        ItemMeta other3ItemMeta = other3.getItemMeta();
        other3ItemMeta.setDisplayName(ChatColor.RED + "Other Illegal Modifications");
        other3ItemMeta.setLore(OtherUtil.stringToLore("The player is using any modifications to the Minecraft client that "
                + "bypass the terms of service of the Reborn Network.", org.bukkit.ChatColor.GRAY));
        other3.setItemMeta(other3ItemMeta);
        inventory.setItem(8, other3);

        // SEV 2
        ItemStack other2 = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta other2ItemMeta = other2.getItemMeta();
        other2ItemMeta.setDisplayName(ChatColor.RED + "Harsh Chat Offenses");
        other2ItemMeta.setLore(OtherUtil.stringToLore("The player may be sending Racist, Offensive, Threatening, or Innapropriate chat messages.", org.bukkit.ChatColor.GRAY));
        other2.setItemMeta(other2ItemMeta);
        inventory.setItem(17, other2);

        // SEV 1
        ItemStack other1 = new ItemStack(Material.PAPER);
        ItemMeta other1ItemMeta = other1.getItemMeta();
        other1ItemMeta.setDisplayName(ChatColor.RED + "Light Chat Offenses");
        other1ItemMeta.setLore(OtherUtil.stringToLore("The player may be using Excessive Capitalization, Hackusating, Spamming or Flooding characters.", org.bukkit.ChatColor.GRAY));
        other1.setItemMeta(other1ItemMeta);
        inventory.setItem(26, other1);


        return inventory;
    }

    public void setVanished(boolean vanished, boolean ignoreDB, boolean effect) {
        this.vanished = vanished;
        if (effect) {
            getPlayer().getLocation().getWorld().strikeLightningEffect(getPlayer().getLocation());
            getPlayer().getLocation().getWorld().strikeLightningEffect(getPlayer().getLocation());
            getPlayer().getLocation().getWorld().strikeLightningEffect(getPlayer().getLocation());
            getPlayer().getLocation().getWorld().strikeLightningEffect(getPlayer().getLocation());
            getPlayer().getLocation().getWorld().strikeLightningEffect(getPlayer().getLocation());
            getPlayer().getLocation().getWorld().playSound(getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }
        if (vanished) {
            RebornCore.getCoveAPI().vanished.add(getUUID());
        } else {
            if (RebornCore.getCoveAPI().vanished.contains(getUUID()))
                RebornCore.getCoveAPI().vanished.remove(getUUID());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.equals(getPlayer()))
                    continue;
                player.showPlayer(getPlayer());
            }
        }
        RebornCore.getCoveAPI().doVanishHides();
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        setVanished(vanished, false, true);
    }

    public void setNick(String name) {
        setNick(name, false);
    }

    public void setNick(String name, boolean ignoreDB) {
        if (!isOnline()) {
            // Try in 2 ticks!
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> setNick(name, ignoreDB), 2L);
            return;
        }
        if (name == null || name.equalsIgnoreCase("")) {
            this.name = null;
            this.nicked = false;
            sendActionBar("");
            DisguiseAPI.undisguiseToAll(getPlayer());
            String prefix;

            if (getServerRank() != ServerRank.DEFAULT) {
                prefix = getServerRank().getTabName();
            } else {
                prefix = getDonorRank().getTabName();
            }

            getPlayer().setPlayerListName(prefix + getName());
            if (!ignoreDB)
                RebornCore.getCoveAPI().runSQLQueryPriority("UPDATE `players` SET `Nick` = '' WHERE `UUID` = '" + getUUID() + "';");
        } else {
            this.name = name;
            this.nicked = true;
            PlayerDisguise playerDisguise = new PlayerDisguise(name, name);
            DisguiseAPI.disguiseToAll(getPlayer(), playerDisguise);
            getPlayer().setPlayerListName(getName());
            if (!ignoreDB)
                RebornCore.getCoveAPI().runSQLQueryPriority("UPDATE `players` SET `Nick` = '" + name + "' WHERE `UUID` = '" + getUUID() + "';");
        }
    }

    public boolean isNicked() {
        return nicked;
    }

    public void getBoosters() {

    }

}
