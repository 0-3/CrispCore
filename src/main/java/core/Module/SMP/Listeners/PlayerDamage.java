package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.SMP.Commands.AdminCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class PlayerDamage implements Listener {

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        if (AdminCommand.admins.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && AdminCommand.admins.contains(event.getDamager().getUniqueId())) {
            event.getDamager().sendMessage(ChatColor.RED + "You can not attack players while in admin mode!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && AdminCommand.admins.contains(event.getTarget().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
