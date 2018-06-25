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
import org.bukkit.util.Vector;

/**
 * Created by ethan on 5/28/2017.
 */
public class SlapCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        RebornPlayer rp = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
        if (!rp.canPlayer(ServerRank.ADMIN)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
        } else {
            if (strings.length == 0) {
                sender.sendMessage(ChatColor.RED + "/slap (player)");
                return true;
            }
            if (Bukkit.getPlayer(strings[0]) == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online");
                return true;
            }
            Player l = Bukkit.getPlayer(strings[0]);
            l.setVelocity(new Vector(0.0, 10.0, 0.0));
            l.sendMessage(ChatColor.BLUE + "SLAP!");
            sender.sendMessage(ChatColor.GREEN + "You have slapped " + ChatColor.BLUE + l.getName());
            Bukkit.getLogger().info(sender.getName() + " slapped " + l.getName());
        }

        return true;
    }
}
