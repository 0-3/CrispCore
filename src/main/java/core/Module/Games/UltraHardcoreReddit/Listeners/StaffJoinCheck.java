package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by ethan on 5/29/2017.
 */
public class StaffJoinCheck implements Listener {

    @EventHandler
    public void onPreJoin(PlayerLoginEvent event) {
        if (RebornCore.getCoveAPI().getCovePlayer(event.getPlayer().getUniqueId()).canPlayer(ServerRank.HELPER)) {
            if (!event.getPlayer().isWhitelisted()) {
                event.getPlayer().setWhitelisted(true);
            }
        }
    }


}
