package network.reborn.core.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setOp(false);
        event.setJoinMessage(null);
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        rebornPlayer.setRunJoin(true);

        if (RebornCore.getCoveAPI().getModule() == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "WARNING: No module is setup on this server");
        } else {
            /** Hoping this doesn't get a massive queue of SQL because of lots of players... We'll see... */
            RebornCore.getCoveAPI().getModule().getCoveServer().syncPlayerCount(false);
//			RebornCore.getRebornAPI().getModule().getCoveServer().pushPlayerCount(false);
        }
        RebornCore.getCoveAPI().doVanishHides();
    }

    @EventHandler
    public void itemDropEvent(PlayerDropItemEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (rebornPlayer.isVanished())
            event.setCancelled(true);
    }

    @EventHandler
    public void pickupEvent(PlayerPickupItemEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (rebornPlayer.isVanished())
            event.setCancelled(true);
    }

}
