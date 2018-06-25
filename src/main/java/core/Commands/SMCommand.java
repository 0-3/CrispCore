package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) sender);
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
            } else {
                ((Player) sender).openInventory(RebornCore.getCoveAPI().getModule().getServerManager());
            }
        }
        return true;
    }

}
