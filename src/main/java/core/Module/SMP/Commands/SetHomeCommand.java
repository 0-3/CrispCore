package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.Module.SMP.Handlers.HomesHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (HomesHandler.getCurrentHomesCount(player) >= HomesHandler.getHomesLimit(player)) {
            if (HomesHandler.getHomesLimit(player) != -1) {
                player.sendMessage(ChatColor.RED + "You can only set " + HomesHandler.getHomesLimit(player) + " homes");
                return true;
            }
        }

        if (args.length != 1) {
            Location location = player.getLocation();
            if (HomesHandler.checkHomeExists("home", player)) {
                player.sendMessage(ChatColor.RED + "You already have a home set, please delete it first \"/delhome home\" or use another name with \"/sethome <name>\"");
                return true;
            }
            HomesHandler.setHome("home", location, player);
            player.sendMessage(ChatColor.GREEN + "Your home has been set");
            return true;
        }

        String name = args[0];
        if (HomesHandler.checkHomeExists(name, player)) {
            player.sendMessage(ChatColor.RED + "You already have a home with that name set, please delete it first using /delhome " + name);
            return true;
        }
        Location location = player.getLocation();
        HomesHandler.setHome(name, location, player);
        player.sendMessage(ChatColor.GREEN + "Home " + name + " has been set");
        return true;
    }

}
