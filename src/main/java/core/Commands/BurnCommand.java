package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BurnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /burn <player> [seconds]");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        int seconds = 5;
        if (args.length > 1) {
            try {
                seconds = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Invalid time provided");
                return true;
            }
        }

        player.setFireTicks(seconds * 20);
        sender.sendMessage(ChatColor.GRAY + "Burning " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " for "
                + ChatColor.YELLOW + seconds + ChatColor.GRAY + " seconds (" + ChatColor.YELLOW + seconds * 20
                + ChatColor.GRAY + " ticks)");
        return true;
    }

}
