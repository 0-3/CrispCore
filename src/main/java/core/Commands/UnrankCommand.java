package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnrankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (args[0].equalsIgnoreCase("Conorz") || args[0].equalsIgnoreCase("ElectronicWizard")
                    || args[0].equalsIgnoreCase("0_3")) {
                sender.sendMessage(ChatColor.RED + "You may not edit the rank of that player");
                return true;
            } else {
                if (rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player> <donor/server>");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("donor")) {
                        if (RebornCore.getCoveAPI().setDefaultRank(args[0], false)) {
                            sender.sendMessage(ChatColor.GREEN + "Updated rank");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Failed to update rank");
                        }
                    } else if (args[1].equalsIgnoreCase("server")) {
                        if (RebornCore.getCoveAPI().setDefaultRank(args[0], true)) {
                            sender.sendMessage(ChatColor.GREEN + "Updated rank");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Failed to update rank");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player> <donor/server>");
                    }
                    return true;
                } else if (rebornPlayer.isPlayer(ServerRank.ADMIN)) {
                    if (args.length < 1) {
                        sender.sendMessage(ChatColor.RED + "Correct usage: /" + string + " <player>");
                        return true;
                    }
                    if (RebornCore.getCoveAPI().setDefaultRank(args[0], true)) {
                        sender.sendMessage(ChatColor.GREEN + "Updated rank");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to update rank");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                }
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
            return true;
        }
    }
}
