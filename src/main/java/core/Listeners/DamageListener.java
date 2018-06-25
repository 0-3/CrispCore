package network.reborn.core.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Events.PlayerCombatEvent;
import network.reborn.core.Events.PlayerDamageByPlayerEvent;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            PlayerDamageEvent playerDamageEvent = new PlayerDamageEvent((Player) event.getEntity());
            Bukkit.getPluginManager().callEvent(playerDamageEvent);
            if (playerDamageEvent.isCanceled())
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            PlayerDamageByPlayerEvent playerDamageByPlayerEvent = new PlayerDamageByPlayerEvent((Player) event.getEntity(), (Player) event.getDamager());
            Bukkit.getPluginManager().callEvent(playerDamageByPlayerEvent);
            if (playerDamageByPlayerEvent.isCanceled()) {
                event.setCancelled(true);
                return;
            }

            PlayerCombatEvent playerCombatEvent = new PlayerCombatEvent((Player) event.getEntity(), true);
            Bukkit.getPluginManager().callEvent(playerCombatEvent);

            playerCombatEvent = new PlayerCombatEvent((Player) event.getDamager(), true);
            Bukkit.getPluginManager().callEvent(playerCombatEvent);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getTarget());
            if (rebornPlayer.isVanished()) {
                event.setCancelled(true);
            }
        }
    }

}
