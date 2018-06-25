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

public class CurrencyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length == 4) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
            switch (args[0].toLowerCase()) {
                default:
                    sender.sendMessage(ChatColor.RED + "/currency <give/take/set> <player> <tag> <amount>");
                    break;
                case "give":
                case "add":
                    int amount;
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "Please enter a valid number");
                        return true;
                    }
                    if (amount < 1) {
                        sender.sendMessage(ChatColor.RED + "Please enter a number greater than 0");
                        return true;
                    }
                    rebornPlayer.giveBalance(args[2], amount);
                    sender.sendMessage(ChatColor.GREEN + "Given to " + player.getName() + " " + amount + " of the currency " + args[2]);
                    break;
                case "take":
                case "remove":
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "Please enter a valid number");
                        return true;
                    }
                    if (amount < 1) {
                        sender.sendMessage(ChatColor.RED + "Please enter a number greater than 0");
                        return true;
                    }
                    rebornPlayer.takeBalance(args[2], amount);
                    sender.sendMessage(ChatColor.GREEN + "Taken from " + player.getName() + " " + amount + " of the currency " + args[2]);
                    break;
                case "set":
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "Please enter a valid number");
                        return true;
                    }
                    if (amount < 0) {
                        sender.sendMessage(ChatColor.RED + "Please enter a number greater or equal to 0");
                        return true;
                    }
                    rebornPlayer.setBalance(args[2], amount);
                    sender.sendMessage(ChatColor.GREEN + "Set balance of " + player.getName() + " to " + amount + " of the currency " + args[2]);
                    break;
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/currency <give/take/set> <player> <tag> <amount>");
        return true;
    }

}
