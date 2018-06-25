package network.reborn.core.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Events.RealPlayerMoveEvent;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerMove implements Listener {
    private static boolean doAfkStuff = true;
    private static HashMap<UUID, Long> lastMove = new HashMap<>();

    public static void doAFKStuff() {
        int afkTime = 300; // 300 seconds till AFK
        Bukkit.getScheduler().runTaskTimerAsynchronously(RebornCore.getRebornCore(), () -> {
            if (!doAfkStuff)
                return;
            Long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            for (Player player : Bukkit.getOnlinePlayers()) {
                RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
                if (!rebornPlayer.isAfk() && lastMove.containsKey(player.getUniqueId()) && lastMove.get(player.getUniqueId()) + afkTime < currentTime) {
                    rebornPlayer.setAfk(true);
                    rebornPlayer.sendTitle(ChatColor.RED + "!!!", ChatColor.AQUA + "You are now AFK", 2, 18, 5);
                } else if (rebornPlayer.isAfk()) {
                    long diff = currentTime - lastMove.get(player.getUniqueId());
                    rebornPlayer.sendTitle(ChatColor.GOLD + "You are AFK", ChatColor.AQUA + OtherUtil.getDurationString(diff), 0, 40, 5);
                }
            }
        }, 20L, 20L);
    }

    public static void setDoAfkStuff(boolean doAfkStuff) {
        PlayerMove.doAfkStuff = doAfkStuff;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            RealPlayerMoveEvent realPlayerMoveEvent = new RealPlayerMoveEvent(event);
            Bukkit.getPluginManager().callEvent(realPlayerMoveEvent);
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
            if (realPlayerMoveEvent.isCanceled()) {
                event.setTo(event.getFrom());
                return;
            } else if (doAfkStuff && rebornPlayer.isAfk()) {
                rebornPlayer.setAfk(false);
                rebornPlayer.sendTitle(ChatColor.GOLD + "AFK", ChatColor.AQUA + "You are no longer AFK", 2, 20, 2);
            }
            lastMove.put(event.getPlayer().getUniqueId(), currentTime);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (lastMove.containsKey(event.getPlayer().getUniqueId()))
            lastMove.remove(event.getPlayer().getUniqueId());
    }

}
