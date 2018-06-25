package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.MathUtils;
import network.reborn.core.Util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tsunami extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    Random r = new Random();
    List<Entity> cooldownJump = new ArrayList<>();
    List<ArmorStand> armorStands = new ArrayList<>();

    public Tsunami() {
        super("Tsunami", "tsunami", Material.WATER_BUCKET, 0);
    }

    @Override
    public void doGadget(final Player player) {
        final Vector v = player.getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = player.getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getType().isSolid()) {
                    loc.add(0, 1, 0);
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                    loc.add(0, -1, 0);
                }
                final ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), EntityType.ARMOR_STAND);
                as.setVisible(false);
                as.setSmall(true);
                as.setGravity(false);
                as.setHeadPose(new EulerAngle(r.nextInt(50), r.nextInt(50), r.nextInt(50)));
                armorStands.add(as);
                for (int i = 0; i < 5; i++) {
                    loc.getWorld().spigot().playEffect(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1.3, 1.8) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), Effect.CLOUD, 0, 0, 0.2f, 0.2f, 0.2f, 0f, 1, 64);
                    loc.getWorld().spigot().playEffect(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), Effect.WATERDRIP, 0, 0, 0.5f, 0.5f, 0.5f, 0.4f, 2, 64);
                }
                float finalR = -255 / 255;
                float finalG = -255 / 255;
                float finalB = 255 / 255;
                for (int a = 0; a < 100; a++)
                    UtilParticles.play(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1, 1.6) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), Effect.COLOURED_DUST, 0, 0, finalR, finalG, finalB, 1f, 0);
                Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
                    @Override
                    public void run() {
                        armorStands.remove(as);
                        as.remove();
                    }
                }, 20);
                for (final Entity ent : as.getNearbyEntities(0.5, 0.5, 0.5)) {
                    if (!cooldownJump.contains(ent) && ent != player && !(ent instanceof ArmorStand)) {
                        MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v.clone().multiply(2)));
                        cooldownJump.add(ent);
                        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
                            @Override
                            public void run() {
                                cooldownJump.remove(ent);
                            }
                        }, 20);
                    }
                }

                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);
    }

}
