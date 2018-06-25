package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class BatBlaster extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public BatBlaster() {
        super("Bat Blaster", "bat-blaster", Material.DIAMOND_BARDING, 0);
        setCooldown(10);
    }

    @Override
    public void doGadget(final Player player) {
        final Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat1 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat2 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat3 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat4 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat5 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat6 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat7 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat8 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);
        final Bat bat9 = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.BAT);

        bat.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat1.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat2.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat3.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat4.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat5.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat6.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat7.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat8.setVelocity(player.getLocation().getDirection().multiply(1.1));
        bat9.setVelocity(player.getLocation().getDirection().multiply(1.1));
        player.playSound(player.getLocation(), Sound.BAT_LOOP, 1, 1);
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final int taskrd = scheduler.scheduleSyncRepeatingTask(RebornCore.getRebornCore(), new Runnable() {
            public void run() {
                bat.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat1.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat2.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat3.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat4.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat5.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat6.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat7.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat8.setVelocity(bat.getLocation().getDirection().multiply(1.1));
                bat9.setVelocity(bat.getLocation().getDirection().multiply(1.1));
            }
        }, 1, 1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), new Runnable() {
            public void run() {
                player.getWorld().playEffect(bat.getLocation(), Effect.POTION_BREAK, 1, 1);
                bat.remove();
                bat1.remove();
                bat2.remove();
                bat3.remove();
                bat4.remove();
                bat5.remove();
                bat6.remove();
                bat7.remove();
                bat8.remove();
                bat9.remove();
                scheduler.cancelTask(taskrd);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

            }
        }, 30);
    }

}
