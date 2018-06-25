package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Module.SMP.Commands.AdminCommand;
import network.reborn.core.Module.SMP.SMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player mcPlayer = event.getPlayer();
        if (AdminCommand.admins.contains(mcPlayer.getUniqueId())) {
            AdminCommand.admins.remove(mcPlayer.getUniqueId());
            if (AdminCommand.inventories.containsKey(mcPlayer.getUniqueId())) {
                mcPlayer.getInventory().setContents(AdminCommand.inventories.get(mcPlayer.getUniqueId()));
                AdminCommand.inventories.remove(mcPlayer.getUniqueId());
            }
        }
        if (SMP.teleportRequests.containsKey(mcPlayer.getUniqueId())) {
            Player target = Bukkit.getPlayer(SMP.teleportRequests.get(mcPlayer.getUniqueId()));
            SMP.teleportRequests.remove(mcPlayer.getUniqueId());
            if (SMP.pendingTeleports.containsKey(mcPlayer.getUniqueId()))
                SMP.pendingTeleports.remove(mcPlayer.getUniqueId());
            target.sendMessage(ChatColor.RED + mcPlayer.getName() + ChatColor.RED + "'s teleport request to you was cancelled because they logged out.");
        } else if (SMP.teleportRequests.containsValue(mcPlayer.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : SMP.teleportRequests.entrySet()) {
                Bukkit.getPlayer(entry.getKey()).sendMessage(ChatColor.RED + "Your teleport request to " + mcPlayer.getName() + ChatColor.RED + " was cancelled because they logged out.");
                if (SMP.pendingTeleports.containsValue(mcPlayer.getUniqueId())) {
                    SMP.pendingTeleports.remove(entry.getKey());
                }
                SMP.teleportRequests.remove(entry.getKey());
            }
        }
    }

}
