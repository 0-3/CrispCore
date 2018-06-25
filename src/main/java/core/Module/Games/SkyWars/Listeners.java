package network.reborn.core.Module.Games.SkyWars;

import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.Games.GamePlayer;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Listeners implements Listener {
    private static HashMap<String, Integer> spawns = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SkyWars.selectedMap.sendPlayerToSpawn(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SkyWars.selectedMap.removePlayerFromSpawns(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDamage(PlayerDamageEvent event) {
        GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(event.getPlayer());
        if (gamePlayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.CHEST) { // TODO, Change it so players can place chests but they don't spawn items!
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (RebornCore.getCoveAPI().getGame().getGameState() != GameState.INGAME) {
            event.setDeathMessage(null);
        } else {
            Player player = event.getEntity();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.setVelocity(new Vector(0, 1, 0));
            if (event.getEntity().getKiller() != null) {
                if (event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause().getCause() != null && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    event.setDeathMessage(ChatColor.RED + event.getEntity().getDisplayName() + " ↢ " + event.getEntity().getKiller().getDisplayName());
                } else {
                    event.setDeathMessage(ChatColor.RED + event.getEntity().getDisplayName() + " was killed by " + event.getEntity().getKiller().getDisplayName());
                }
            }
        }
    }

}
