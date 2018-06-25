package network.reborn.core.Commands;

import network.reborn.core.API.DonorRank;
import network.reborn.core.API.Module;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (RebornCore.getCoveAPI().getModule().getModule() == Module.HUB) {
                if (!rebornPlayer.canPlayer(DonorRank.VIPPLUS) && !rebornPlayer.canPlayer(ServerRank.HELPER)) {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                    return true;
                }
            } else {
                if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                    return true;
                }
            }
            if (rebornPlayer.isFlying()) {
                ((Player) sender).setFlying(false);
                ((Player) sender).setAllowFlight(false);
                rebornPlayer.setFlying(false);
                sender.sendMessage(ChatColor.GRAY + "Flying has been " + ChatColor.YELLOW + "disabled");
            } else {
                ((Player) sender).setAllowFlight(true);
                rebornPlayer.setFlying(true);
                sender.sendMessage(ChatColor.GRAY + "Flying has been " + ChatColor.YELLOW + "enabled");
            }
        }
        return true;
    }

}
