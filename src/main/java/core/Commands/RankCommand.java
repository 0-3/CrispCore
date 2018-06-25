package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            Player player = (Player) sender;
            if (rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                if (args.length > 2 || args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player>");
                    return true;
                } else if (args.length == 2) {
                    if (RebornCore.getCoveAPI().updatePlayerRank(args[0], args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Updated rank");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to update rank");
                    }
                } else if (args.length == 1) {
                    player.openInventory(rebornPlayer.setRankGUI(args[0]));
                } else {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player>");
                    return true;
                }
            } else if (rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player>");
                    return true;
                } else {
                    player.openInventory(rebornPlayer.setRankGUI(args[0]));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }

        } else {
            if (!(args.length == 2)) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player> <rank>");
                return true;
            } else {
                if (RebornCore.getCoveAPI().updatePlayerRank(args[0], args[1])) {
                    sender.sendMessage(ChatColor.GREEN + "Updated rank");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to update rank");
                }
            }
        }
        return true;
    }

}
