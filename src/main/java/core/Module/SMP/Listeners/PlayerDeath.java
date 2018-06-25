package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getEntity());
        String deathMessage = event.getDeathMessage();
        if (rebornPlayer.isNicked()) {
            deathMessage = deathMessage.replaceAll(event.getEntity().getName(), rebornPlayer.getName());
        }

        if (event.getEntity().getKiller() != null) {
            RebornPlayer coveKiller = RebornCore.getCoveAPI().getCovePlayer(event.getEntity().getKiller());
            if (coveKiller.isNicked()) {
                deathMessage = deathMessage.replaceAll(event.getEntity().getKiller().getName(), coveKiller.getName());
            }
        }
        event.setDeathMessage(deathMessage);

    }

}
