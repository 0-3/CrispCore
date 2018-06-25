package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.BlockUtils;
import network.reborn.core.Util.ItemFactory;
import network.reborn.core.Util.MathUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DiscoBall extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    Random r = new Random();
    int i = 0;
    double i2 = 0;
    ArmorStand armorStand;
    boolean running = false;

    public DiscoBall() {
        super("Disco Ball", "disco-ball", Material.BEACON, 0);
        setCooldown(30);
    }

    @Override
    public void doGadget(final Player player) {
        if (running) {
            player.sendMessage(ChatColor.RED + "A disco ball already exists");
            return;
        }
        armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 3, 0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(false);
        armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
        running = true;
//        Core.discoBalls.add(this);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                onUpdate();
            }
        };
        runnable.runTaskTimerAsynchronously(RebornCore.getRebornCore(), 0, 1);

        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new BukkitRunnable() {
            @Override
            public void run() {
                running = false;
                armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), Effect.STEP_SOUND, Material.STAINED_CLAY.getId(), 4, 0, 0, 0, 1, 200, 32);
                armorStand.remove();
                armorStand = null;
                i = 0;
                i2 = 0;
            }
        }, 20 * 20);
    }

    public void onUpdate() {
        if (running) {
            armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.2, 0));
            armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.SPELL, 0, 0, 0, 0, 0, 1f, 1, 64);
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 1f, 1, 64);
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.NOTE, r.nextInt(15), r.nextInt(15), 4, 3, 4, 1f, 1, 64);
            double angle, angle2, x, x2, z, z2;
            angle = 2 * Math.PI * i / 100;
            x = Math.cos(angle) * 4;
            z = Math.sin(angle) * 4;
            drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).clone().add(x, 0, z), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.POTION_SWIRL, 20);
            i += 6;
            angle2 = 2 * Math.PI * i2 / 100;
            x2 = Math.cos(angle2) * 4;
            z2 = Math.sin(angle2) * 4;
            drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).add(x2, 0, z2), Effect.COLOURED_DUST, 50);
            i2 += 0.4;
            for (Entity ent : getNearbyEntities(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), 7.5))
                if (ent.isOnGround() && true) // affect players?
                    MathUtils.applyVelocity(ent, new Vector(0, 0.3, 0));


            for (Block b : BlockUtils.getBlocksInRadius(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), 10, false))
                if (b.getType() == Material.WOOL || b.getType() == Material.CARPET)
                    BlockUtils.setToRestore(b, b.getType(), (byte) r.nextInt(15), 4);

        }
    }

    public ArrayList<Entity> getNearbyEntities(Location loc, double distance) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity ent : loc.getWorld().getEntities()) {
            if (ent.getLocation().distance(loc) <= distance) {
                entities.add(ent);
            }
        }
        return entities;
    }

    public void drawParticleLine(Location a, Location b, Effect effect, int particles) {
        Location location = a.clone();
        Location target = b.clone();
        double amount = particles;
        Vector link = target.toVector().subtract(location.toVector());
        float length = (float) link.length();
        link.normalize();

        float ratio = length / particles;
        Vector v = link.multiply(ratio);
        if (effect == Effect.POTION_SWIRL)
            MathUtils.rotateAroundAxisX(v, i);
        else {
            MathUtils.rotateAroundAxisZ(v, i2 / 5);
            MathUtils.rotateAroundAxisX(v, i2 / 5);
        }
        Location loc = location.clone().subtract(v);
        int step = 0;
        for (int i = 0; i < particles; i++) {
            if (step >= amount)
                step = 0;
            step++;
            loc.add(v);
            if (effect == Effect.COLOURED_DUST) {
                location.getWorld().spigot().playEffect(loc, effect, 0, 0, 0, 0, 0, 0, 1, 32);
                continue;
            }
            location.getWorld().spigot().playEffect(loc, effect);
        }
    }

}
