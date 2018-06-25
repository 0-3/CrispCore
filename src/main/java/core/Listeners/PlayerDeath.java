package network.reborn.core.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getEntity());
        if (!rebornPlayer.isNicked())
            return;
        String message = event.getDeathMessage();

        if (event.getEntity().getKiller() != null) {
            // Was killed by another player, do slain message
            RebornPlayer coveKiller = RebornCore.getCoveAPI().getCovePlayer(event.getEntity().getKiller());
            message = rebornPlayer.getName() + " was slain by " + coveKiller.getName();

            if (!rebornPlayer.isNicked()) {
//				if (!RebornCore.achievementHandler.getKillOwner().alreadyEarned(coveKiller))
//					RebornCore.achievementHandler.getKillOwner().giveAchievement(coveKiller);
//				else if (!RebornCore.achievementHandler.getKillDeveloper().alreadyEarned(coveKiller))
//					RebornCore.achievementHandler.getKillDeveloper().giveAchievement(coveKiller);
            }
        } else if (message != null && !message.equalsIgnoreCase("")) {
            message = message.replaceAll(event.getEntity().getName(), rebornPlayer.getName()); // Last resort to try and hide names
        }
        event.setDeathMessage(message);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (rebornPlayer.isNicked()) {
            rebornPlayer.setNick(rebornPlayer.getName(), true);
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> rebornPlayer.setNick(rebornPlayer.getName(), true), 5L);
        }
    }

}
