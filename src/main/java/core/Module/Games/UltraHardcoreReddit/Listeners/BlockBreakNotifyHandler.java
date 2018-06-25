package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class BlockBreakNotifyHandler implements Listener {

    private static HashMap<String, Integer> diamondsMined = new HashMap<String, Integer>();

    @EventHandler
    public void onBreakEvent(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.DIAMOND_ORE)) {
            Player player = e.getPlayer();
            if (diamondsMined.containsKey(player.getName())) {
                int d = diamondsMined.get(player.getName());
                d++;
                diamondsMined.replace(player.getName(), d);
            } else {
                diamondsMined.put(player.getName(), 1);
            }
            int diamonds = diamondsMined.get(player.getName());
            for (Player p : Bukkit.getOnlinePlayers()) {
                RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((p).getUniqueId());
                if (rebornPlayer.canPlayer(ServerRank.HELPER) && p.getGameMode().equals(GameMode.SPECTATOR)) {
                    rebornPlayer.getPlayer().sendMessage(ChatColor.YELLOW + player.getName()
                            + ChatColor.GRAY + " has found a diamond (" + ChatColor.YELLOW
                            + diamonds + ChatColor.GRAY + ")");
                }
            }
        }
    }
}