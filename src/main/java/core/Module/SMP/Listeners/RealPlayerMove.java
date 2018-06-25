package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Events.RealPlayerMoveEvent;
import network.reborn.core.Module.SMP.SMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RealPlayerMove implements Listener {

    @EventHandler
    public void onRealPlayerMove(RealPlayerMoveEvent event) {
        if (SMP.teleportList.contains(event.getPlayer().getUniqueId())) {
            SMP.teleportList.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.RED + "Teleport cancelled!");
        }
        if (SMP.pendingTeleports.containsKey(event.getPlayer().getUniqueId())) {
            Bukkit.getPlayer(SMP.pendingTeleports.get(event.getPlayer().getUniqueId())).sendMessage(ChatColor.RED + event.getPlayer().getName() + ChatColor.RED + " has cancelled their teleport to you.");
            SMP.pendingTeleports.remove(event.getPlayer().getUniqueId());
            SMP.teleportRequests.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.RED + "Teleport cancelled!");
        }
    }

}
