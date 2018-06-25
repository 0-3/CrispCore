package network.reborn.core.Module.Games.UltraHardcoreReddit.UBL;

import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Created by ethan on 5/29/2017.
 */
public class PlayerJoinAsyncProcessing implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final UUID u = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(RebornCore.getRebornCore(), () -> {
            if (CheckLocalUBL.isBanned(u)) {
                Bukkit.getScheduler().runTask(RebornCore.getRebornCore(), () -> {
                    UltraHardcoreReddit.disconnectUBL(u);
                });
            }
        });
    }

}
