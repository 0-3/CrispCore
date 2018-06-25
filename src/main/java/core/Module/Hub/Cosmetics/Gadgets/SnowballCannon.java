package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;

public class SnowballCannon extends Gadget implements Listener {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public SnowballCannon() {
        super("Snowball Cannon", "snowball-cannon", Material.IRON_BARDING, 0);
    }

    @Override
    public void doGadget(final Player player) {
        player.launchProjectile(Snowball.class);
    }

    @EventHandler
    public void snowball(ProjectileHitEvent e) {
        Entity entity = e.getEntity();
        Player p = (Player) e.getEntity().getShooter();
        if (entity.getType() == EntityType.SNOWBALL) {
            Location loc = e.getEntity().getLocation();
            loc.setY(loc.getY() - 1);
            int id = OtherUtil.randInt(1, 3);

            Location loc2 = e.getEntity().getLocation();
            loc2.setX(loc2.getX() - 1);

            Location loc3 = e.getEntity().getLocation();
            loc3.setX(loc3.getX() + 1);

            Location loc4 = e.getEntity().getLocation();
            loc4.setZ(loc4.getZ() - 1);

            Location loc5 = e.getEntity().getLocation();
            loc5.setZ(loc5.getZ() + 1);

            if (loc.getBlock().getType() != Material.AIR) {
                if (id == 1) {
                    p.sendBlockChange(loc, Material.ICE, (byte) 0);
                } else if (id == 2) {
                    p.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                } else if (id == 3) {
                    p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                }
            }
            id = OtherUtil.randInt(1, 3);
            if (loc2.getBlock().getType() != Material.AIR) {
                if (id == 1) {
                    p.sendBlockChange(loc, Material.ICE, (byte) 0);
                } else if (id == 2) {
                    p.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                } else if (id == 3) {
                    p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                }
            }
            id = OtherUtil.randInt(1, 3);
            if (loc3.getBlock().getType() != Material.AIR) {
                if (id == 1) {
                    p.sendBlockChange(loc, Material.ICE, (byte) 0);
                } else if (id == 2) {
                    p.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                } else if (id == 3) {
                    p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                }
            }
            id = OtherUtil.randInt(1, 3);
            if (loc4.getBlock().getType() != Material.AIR) {
                if (id == 1) {
                    p.sendBlockChange(loc, Material.ICE, (byte) 0);
                } else if (id == 2) {
                    p.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                } else if (id == 3) {
                    p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                }
            }
            id = OtherUtil.randInt(1, 3);
            if (loc5.getBlock().getType() != Material.AIR) {
                if (id == 1) {
                    p.sendBlockChange(loc, Material.ICE, (byte) 0);
                } else if (id == 2) {
                    p.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                } else if (id == 3) {
                    p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                }
            }
        }
    }

}
