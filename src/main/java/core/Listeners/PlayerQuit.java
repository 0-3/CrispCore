package network.reborn.core.Listeners;

import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        RebornCore.getCoveAPI().getModule().getCoveServer().updatePlayerCount(Bukkit.getOnlinePlayers().size() - 1, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitMonitor(PlayerQuitEvent event) {
        RebornCore.getCoveAPI().removeCovePlayer(event.getPlayer().getUniqueId());
        RebornCore.getCoveAPI().removeGamePlayer(event.getPlayer().getUniqueId());
    }

}
