package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by ethan on 12/28/2017.
 */
public class PlayerDeathKickHandler implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.INGAME)) {
            return;
        }
        final Player player = event.getEntity();
        final RebornPlayer rp = RebornCore.getCoveAPI().getCovePlayer(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> {
            player.spigot().respawn();
            player.teleport(RebornCore.getCoveAPI().getGame().getGameSettings().getGameLobby());
            rp.sendCentredMessage(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");
            rp.sendCentredMessage(ChatColor.RED + "You died!");
            rp.sendCentredMessage(ChatColor.GREEN + "Thanks for playing on Reborn Network!");
            rp.getPlayer().sendMessage("");
            rp.sendCentredMessage(ChatColor.YELLOW + "Want to hear about our upcoming matches?");
            rp.sendCentredMessage(ChatColor.YELLOW + "Follow us on Twitter " + ChatColor.AQUA + "@RebornNetwork_");
            rp.getPlayer().sendMessage("");
            rp.sendCentredMessage(ChatColor.GRAY + "You will be sent back to Lobby in 10 seconds.");
            rp.sendCentredMessage(ChatColor.GRAY + "If you are a VIP or higher, you can");
            rp.sendCentredMessage(ChatColor.GRAY + "rejoin to spectate the match around 0,0.");
            rp.sendCentredMessage(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> {
            UltraHardcoreReddit.sendUHCMessage(ChatColor.GRAY + "Sending you to Lobby...", player);
            rp.sendToRandomHub();
        }, 210L);

    }


}
