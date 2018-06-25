package network.reborn.core.Module.Games.UltraHardcoreReddit.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.PlayerStats;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.RedditEngine;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Created by ethan on 2/3/2017.
 */
public class StatsCommand extends AbstractCommand {
    public StatsCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sender must be player!");
            return true;
        }
        RebornPlayer rp = RebornCore.getCoveAPI().getCovePlayer(((Player) sender));
        PlayerStats stats = RedditEngine.db.getStats(((Player) sender).getUniqueId().toString());

        if (args.length == 1) {
            String name = args[0];
            if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).getName() != null && !Objects.equals(Bukkit.getOfflinePlayer(name).getName(), "")) {
                stats = RedditEngine.db.getStats(Bukkit.getOfflinePlayer(name).getUniqueId().toString());
            } else {
                PlayerStats.sendMessage(rp.getPlayer(), ChatColor.RED + "That player has no tracked UHC stats!");
                return true;
            }
        }
        String display = args.length == 1 ? Bukkit.getOfflinePlayer(args[0]).getName() : rp.getName();
        PlayerStats.sendMessage(rp.getPlayer(), ChatColor.AQUA + display);
        PlayerStats.sendMessage(rp.getPlayer(), ChatColor.YELLOW + "Wins: " + ChatColor.GREEN + stats.getWins() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Losses: " + ChatColor.GREEN + stats.getLosses() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Kills: " + ChatColor.GREEN + stats.getKills());


        return true;
    }
}
