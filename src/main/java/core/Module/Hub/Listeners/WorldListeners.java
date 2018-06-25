package network.reborn.core.Module.Hub.Listeners;

import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldListeners implements Listener {
    private Hub hub;

    public WorldListeners(Hub hub) {
        this.hub = hub;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        ((Player) event.getEntity()).setSaturation(20);
        ((Player) event.getEntity()).setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Bukkit.getScheduler().runTaskAsynchronously(RebornCore.getRebornCore(), () -> {
            int x = chunk.getX() << 4;
            int z = chunk.getZ() << 4;
            World world = chunk.getWorld();
            for (int xx = x; xx < x + 16; xx++) {
                for (int zz = z; zz < z + 16; zz++) {
                    for (int yy = 0; yy < 256; yy++) {
                        Block block = world.getBlockAt(xx, yy, zz);
                        if (Hub.signs.containsKey(block.getLocation()))
                            continue;
                        if (block.getType().toString().contains("SIGN")) {
                            Sign sign = (Sign) block.getState();
                            if (sign.getLine(0).contains("[SIGN]") && !sign.getLine(2).equals("RANDOM")) {
                                network.reborn.core.API.Module module = network.reborn.core.API.Module.valueOf(sign.getLine(1).toUpperCase());
                                Hub.signs.put(block.getLocation(), module);
                            } else if (sign.getLine(0).equals("[BEACON]")) {
                                Hub.beacons.add(block.getLocation());
                            } else if (sign.getLine(0).equals("[VIPDOOR]")) {
                                Hub.vipBlocks.add(block.getLocation());
                            } else if (sign.getLine(0).equals("[PARKOUR]")) {
                                switch (sign.getLine(1)) {
                                    case "start":
                                        Hub.parkourStart.add(sign.getLocation());
                                        break;
                                    case "end":
                                        Hub.parkourEnd.add(sign.getLocation());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }

        });
    }

}
