package network.reborn.core.Util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class OtherUtil {

    @Deprecated
    public static String getCurrentDateToMySQL() {
        return getCurrentDateTimeToMySQL();
    }

    public static String getCurrentDateTimeToMySQL() {
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static List<String> stringToLore(String string, ChatColor chatColor) {
        return stringToLore(string, 32, true, chatColor);
    }

    public static List<String> stringToLore(String string, int size, ChatColor chatColor) {
        return stringToLore(string, size, true, chatColor);
    }

    public static List<String> stringToLore(String string, boolean blankLines, ChatColor chatColor) {
        return stringToLore(string, 32, blankLines, chatColor);
    }

    public static List<String> stringToLore(String string, int size, boolean blankLines, ChatColor chatColor) {
        StringTokenizer tok = new StringTokenizer(string, " ");
        StringBuilder output = new StringBuilder(string.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while (word.length() > size) {
                output.append(word.substring(0, size - lineLen) + "\n");
                word = word.substring(size - lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > size) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }

        // Convert to List
        List<String> list = new ArrayList<>();
        if (blankLines)
            list.add("");
        for (String s : output.toString().split("\n")) {
            list.add(chatColor + s);
        }
        if (blankLines)
            list.add("");
        return list;
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    public static void changeChestState(Location block, boolean open, Player player) { // Let's take a moment of silence for Connor who spent hours working on this wanting to shoot himself cause he knows nothing when it comes to packets RIP
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer blockAction = protocolManager.createPacket(PacketType.Play.Server.BLOCK_ACTION);
        blockAction.getBlockPositionModifier().write(0, new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ()));
        blockAction.getIntegers().write(0, 1).write(1, (open) ? 1 : 0);
        blockAction.getBlocks().write(0, Material.CHEST);
        try {
            protocolManager.sendServerPacket(player, blockAction);
        } catch (InvocationTargetException ignored) {
        }
    }

    public static String getDurationString(long seconds) {
        return getDurationString(seconds, false);
    }

    public static String getDurationString(long seconds, boolean shortFormat) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        String returnString;
        if (shortFormat) {
            if (hours > 0)
                returnString = hours + "h" + minutes + "m" + seconds + "s";
            else if (minutes > 0)
                returnString = minutes + "m" + seconds + "s";
            else
                returnString = seconds + "s";
        } else {
            if (hours > 0)
                returnString = hours + (hours == 1 ? " hour, " : " hours, ") + minutes + (minutes == 1 ? " minute" : " minutes") + " & " + seconds + (seconds == 1 ? " second" : " seconds");
            else if (minutes > 0)
                returnString = minutes + (minutes == 1 ? " minute" : " minutes") + " & " + seconds + (seconds == 1 ? " second" : " seconds");
            else
                returnString = seconds + (seconds == 1 ? " second" : " seconds");
        }
        return returnString;
    }

    public static boolean deleteWorld(String name) {
        World world = Bukkit.getServer().getWorld(name);
        if (world != null) {
            // Kicks any players for reset
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName().equals(world.getName())).forEach(player -> player.kickPlayer(""));
            Bukkit.unloadWorld(world, false);
        }

        File directory = new File(name);
        if (directory.exists()) {
            try {
                Bukkit.getServer().getLogger().info("Deleting world...");
                deleteFiles(directory);
                Bukkit.getServer().getLogger().info("Deleted world");
                return true;
            } catch (IOException ignored) {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static void deleteFiles(File file) throws IOException {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    deleteFiles(fileDelete);
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public static String formatNumber(double integer) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(integer);
    }

}
