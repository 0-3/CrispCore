package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.PlayerStats;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.RedditEngine;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by ethan on 1/21/2017.
 */
public class PlayerJoinStatsHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(RebornCore.getRebornCore(), () -> {
            PlayerStats stats = RedditEngine.db.getStats(event.getPlayer().getUniqueId().toString());
            RebornPlayer rp = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
            if (stats.getUUID().equalsIgnoreCase("")) {
                rp.sendCentredMessage(ChatColor.GOLD + "●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
                rp.sendCentredMessage(ChatColor.RED + "Welcome to Reddit UHC!");
                rp.sendCentredMessage(ChatColor.GRAY + "");
                rp.sendCentredMessage(ChatColor.YELLOW + "It appears as though we don't have");
                rp.sendCentredMessage(ChatColor.YELLOW + "any previous stats for you! Let's");
                rp.sendCentredMessage(ChatColor.YELLOW + "set the default values, then.");
                rp.sendCentredMessage(ChatColor.RED + "");
                RedditEngine.db.setDefaultData(event.getPlayer().getUniqueId().toString());
                rp.sendCentredMessage(ChatColor.GRAY + "Updated stats. New stats:");
                rp.sendCentredMessage(ChatColor.YELLOW + "Wins: " + ChatColor.GREEN + "0" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Losses: " + ChatColor.GREEN + "0" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Kills: " + ChatColor.GREEN + "0");
                rp.sendCentredMessage(ChatColor.GOLD + "●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
            } else {
                rp.sendCentredMessage(ChatColor.GOLD + "●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
                rp.sendCentredMessage(ChatColor.RED + "Welcome to Reddit UHC!");
                rp.sendCentredMessage(ChatColor.GRAY + "");
                rp.sendCentredMessage(ChatColor.YELLOW + "Loaded in your stats.");
                rp.sendCentredMessage(ChatColor.RED + "");
                rp.sendCentredMessage(ChatColor.YELLOW + "Wins: " + ChatColor.GREEN + stats.getWins() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Losses: " + ChatColor.GREEN + stats.getLosses() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Kills: " + ChatColor.GREEN + stats.getKills());
                rp.sendCentredMessage(ChatColor.GOLD + "●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
            }
        });
    }
}
