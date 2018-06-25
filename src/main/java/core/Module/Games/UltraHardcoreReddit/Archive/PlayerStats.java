package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.RedditEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ethan on 1/20/2017.
 */
public class PlayerStats {

    private static ArrayList<PlayerStats> statsToUpload = new ArrayList<>();
    private String uuid;
    private int wins;
    private int losses;
    private int kills;


    public PlayerStats(String uuid, int wins, int losses, int kills) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
    }

    public static void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Stats " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    public static void queueStat(PlayerStats stat) {
        statsToUpload.add(stat);
    }

    public static void unqueueStat(UUID uuid) {
        for (PlayerStats s : statsToUpload) {
            if (s.getUUID().equalsIgnoreCase(uuid.toString())) {
                statsToUpload.remove(s);
                break;
            }
        }
    }

    public static void uploadAllStats() {
        for (PlayerStats s : statsToUpload) {
            uploadStat(s);
        }
        statsToUpload.clear();
        Bukkit.getLogger().info("PlayerStats // All statistics have been updated.");
    }

    public static void uploadStat(PlayerStats stat) {
        RedditEngine.db.updateStats(stat.getUUID(), stat.getWins(), stat.getLosses(), stat.getKills());
    }

    public String getUUID() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int i) {
        wins = wins + i;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int i) {
        losses = losses + i;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int i) {
        kills = kills + i;
    }


}
