package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Listeners.PlayerChat;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ethan on 12/19/2016.
 */
public class NameColorCommand extends AbstractCommand {
    public NameColorCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RebornPlayer rp = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
        if (rp.canPlayer(ServerRank.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage("Valid colors: RED,DARK_RED,BLUE,DARK_BLUE,AQUA,DARK_AQUA,GREEN,DARK_GREEN,GOLD,LIGHT_PURPLE,DARK_PURPLE,YELLOW,GRAY,DARK_GRAY,BLACK,WHITE");
                sender.sendMessage(ChatColor.RED + "Correct usage: /namecolor <color>");
            } else {
                try {
                    String s = args[0].toUpperCase();
                    ChatColor i = ChatColor.valueOf(s);
                    PlayerChat.colors.put(((Player) sender).getUniqueId(), i);
                    sender.sendMessage(i + "Name color set.");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Invalid color");
                }
            }
        } else if (rp.canPlayer(ServerRank.HELPER)) {
            if (args.length == 0) {
                sender.sendMessage("Valid colors: AQUA, LIGHT_PURPLE, YELLOW, WHITE");
                sender.sendMessage(ChatColor.RED + "Correct usage: /namecolor <color>");
            } else if (args[0].equalsIgnoreCase("yellow") || args[0].equalsIgnoreCase("white") ||
                    args[0].equalsIgnoreCase("aqua") || args[0].equalsIgnoreCase("light_purple")) {
                try {
                    String s = args[0].toUpperCase();
                    ChatColor i = ChatColor.valueOf(s);
                    PlayerChat.colors.put(((Player) sender).getUniqueId(), i);
                    sender.sendMessage(i + "Name color set.");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Invalid color");
                }
            } else {
                sender.sendMessage("Valid colors: AQUA, LIGHT_PURPLE, YELLOW, WHITE");
                sender.sendMessage(ChatColor.RED + "Correct usage: /namecolor <color>");
            }
        } else if (rp.canPlayer(ServerRank.MEDIA)) {
            if (args.length == 0) {
                sender.sendMessage("Valid colors: YELLOW, WHITE");
                sender.sendMessage(ChatColor.RED + "Correct usage: /namecolor <color>");
            } else if (args[0].equalsIgnoreCase("yellow") || args[0].equalsIgnoreCase("white")) {
                try {
                    String s = args[0].toUpperCase();
                    ChatColor i = ChatColor.valueOf(s);
                    PlayerChat.colors.put(((Player) sender).getUniqueId(), i);
                    sender.sendMessage(i + "Name color set.");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Invalid color");
                }
            } else {
                sender.sendMessage("Valid colors: YELLOW, WHITE");
                sender.sendMessage(ChatColor.RED + "Correct usage: /namecolor <color>");
            }
        }
        return true;
    }
}
