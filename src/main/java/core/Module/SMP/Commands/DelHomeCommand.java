package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.Module.SMP.Handlers.HomesHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/delhome <name>");
            return true;
        }

        String name = args[0];
        if (HomesHandler.checkHomeExists(name, player)) {
            HomesHandler.deleteHome(name, player);
            player.sendMessage(ChatColor.GREEN + "Home " + name + " has been deleted");
        } else {
            player.sendMessage(ChatColor.RED + "Home not found");
        }
        return true;
    }

}
