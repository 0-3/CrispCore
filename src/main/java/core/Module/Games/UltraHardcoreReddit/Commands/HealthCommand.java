package network.reborn.core.Module.Games.UltraHardcoreReddit.Commands;

import network.reborn.core.Util.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

/**
 * Created by ethan on 2/7/2017.
 */
public class HealthCommand extends AbstractCommand {
    public HealthCommand(String command) {
        super(command);
    }

    void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Health " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = null;
        if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                p = Bukkit.getPlayer(args[0]);
            } else {
                sendMessage((Player) sender, ChatColor.RED + "That player is not online.");
                return true;
            }
        } else {
            p = (Player) sender;
        }
        DecimalFormat df = new DecimalFormat("##.##");
        sendMessage((Player) sender, ChatColor.AQUA + p.getName() + ChatColor.YELLOW + "'s health: " + ChatColor.GREEN + Double.valueOf(df.format(p.getHealth())) + "/" + p.getMaxHealth());
        return true;
    }
}
