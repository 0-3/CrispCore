package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            Player mcPlayer = (Player) sender;
            if (SMP.teleportList.contains(mcPlayer.getUniqueId())) {
                mcPlayer.sendMessage(ChatColor.RED + "You are already waiting to teleport");
                return true;
            }
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                mcPlayer.sendMessage(ChatColor.GOLD + "Teleporting in 3 seconds. Don't move!");
                SMP.teleportList.add(mcPlayer.getUniqueId());
            }
            // TODO Improve the teleport delay as if you do it quick enough you can teleport in less than 3 seconds...
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
                if (!SMP.teleportList.contains(mcPlayer.getUniqueId()) && !rebornPlayer.canPlayer(ServerRank.ADMIN))
                    return;
                SMP.teleportList.remove(mcPlayer.getUniqueId());
                if (!mcPlayer.isOnline())
                    return;
                mcPlayer.sendMessage(ChatColor.GOLD + "Teleporting...");
                mcPlayer.teleport(SMP.spawn);
            }, rebornPlayer.canPlayer(ServerRank.ADMIN) ? 1L : 20L * 3);
            return true;
        }
        return true;
    }
}