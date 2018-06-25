package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Events.HelpCommandEvent;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player))
            return false;
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) sender);
        HelpCommandEvent helpCommandEvent = new HelpCommandEvent(sender, args);
        Bukkit.getPluginManager().callEvent(helpCommandEvent);
        if (!helpCommandEvent.isCanceled()) {
            // Generate help from hash map
            if (!helpCommandEvent.getCommands().isEmpty()) {
                rebornPlayer.sendCentredMessage(ChatColor.AQUA + "");
                rebornPlayer.sendCentredMessage(ChatColor.AQUA + "Reborn Network Help");
                for (Map.Entry<String, String> entry : helpCommandEvent.getCommands().entrySet()) {
                    sender.sendMessage(ChatColor.GREEN + "/" + entry.getKey() + ChatColor.AQUA + " - " + entry.getValue());
                }
            } else {
                rebornPlayer.sendCentredMessage(ChatColor.AQUA + "");
                rebornPlayer.sendCentredMessage(ChatColor.RED + "Hmm... either you have literally" + ChatColor.BOLD + " no " + ChatColor.RESET + "" + ChatColor.RED + "command access, or this feature still isn't finished! Check back later.");
            }
        }
        return true;
    }

}
