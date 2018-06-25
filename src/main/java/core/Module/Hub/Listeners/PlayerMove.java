package network.reborn.core.Module.Hub.Listeners;

import network.reborn.core.API.DonorRank;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private Hub hub;

    public PlayerMove(Hub hub) {
        this.hub = hub;
    }

    private static boolean hasBarrier(Location origin) {
        int x = origin.getBlockX();
        int y = origin.getBlockY();
        int z = origin.getBlockZ();
        World world = origin.getWorld();

        for (int cy = 2; cy < 512; cy++) {
            int testY;
            if ((cy & 1) == 0) {
                testY = y + cy / 2;
                if (testY > 255) {
                    continue;
                }
            } else {
                testY = y - cy / 2;
                if (testY < 0) {
                    continue;
                }
            }
            if (world.getBlockAt(x, testY, z).getType().equals(Material.SPONGE)) {
                return true;
            }

        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player mcplayer = event.getPlayer();
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(mcplayer);
        Location location = event.getPlayer().getLocation();
        if (hasBarrier(location)) {
            if (!rebornPlayer.canPlayer(DonorRank.VIP))
                rebornPlayer.getPlayer().teleport(new Location(rebornPlayer.getPlayer().getWorld(), -288.5, 13.5, 116.5, 0, 0));
        }
    }

}
