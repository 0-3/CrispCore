package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) sender);
            if (args.length != 1) {
                Bukkit.getLogger().info("Args != 1");
                sender.sendMessage(ChatColor.RED + "Correct Usage: /report <player>");
                return true;
            }
            rebornPlayer.getPlayer().openInventory(rebornPlayer.getReportGUI(args[0]));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Reporting is only supported by players at the moment!");
            return true;
        }
    }

}
