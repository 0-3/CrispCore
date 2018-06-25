package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Module.SMP.SMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Respawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getBedSpawnLocation() != null)
            event.setRespawnLocation(event.getPlayer().getBedSpawnLocation());
        else
            event.setRespawnLocation(SMP.spawn);
    }

}
