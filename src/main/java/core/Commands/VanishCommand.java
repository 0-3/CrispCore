package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
            if (rebornPlayer.isVanished()) {
                rebornPlayer.setVanished(false, false, false);
                sender.sendMessage(ChatColor.RED + "You have been un-vanished");
            } else {
                rebornPlayer.setVanished(true, false, false);
                sender.sendMessage(ChatColor.GREEN + "You have been vanished");
            }
        }
        return true;
    }

}
