package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.Module.SMP.Handlers.HomesHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        Integer currentHomes = HomesHandler.getCurrentHomesCount(player);
        if (args.length != 1) {
            String response = HomesHandler.getHomes(player);
            if (response.equalsIgnoreCase("")) {
                player.sendMessage(ChatColor.RED + "No homes are currently set");
            } else if (currentHomes == 1 && !string.equalsIgnoreCase("homes")) {
                HomesHandler.teleportPlayerToFirstHome(player);
            } else {
                player.sendMessage(ChatColor.GREEN + "Current Homes: " + response);
            }
            return true;
        }

        String name = args[0];
        if (HomesHandler.checkHomeExists(name, player)) {
            HomesHandler.teleportPlayer(name, player);
        } else {
            player.sendMessage(ChatColor.RED + "Home not found");
        }
        return true;
    }

}
