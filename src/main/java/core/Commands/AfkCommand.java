package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.HELPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
            if (rebornPlayer.isAfk()) {
                rebornPlayer.setAfk(false);
                sender.sendMessage(ChatColor.RED + "You are no longer AFK");
            } else {
                rebornPlayer.setAfk(true);
                sender.sendMessage(ChatColor.GREEN + "You are now AFK");
            }
        }
        return true;
    }

}
