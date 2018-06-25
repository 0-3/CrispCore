package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.MathUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ExplosiveSheep extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    ArrayList<Sheep> sheepArrayList = new ArrayList<>();

    public ExplosiveSheep() {
        super("Explosive Sheep", "explosive-sheep", Material.SHEARS, 0);
        setCooldown(20);
    }

    @Override
    public void doGadget(final Player player) {
        Location loc = player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.5));
        loc.setY(player.getLocation().getBlockY() + 1);
        Sheep s = player.getWorld().spawn(loc, Sheep.class);

        s.setNoDamageTicks(100000);
        sheepArrayList.add(s);

//        EntitySheep entitySheep = ((CraftSheep) s).getHandle();
//
//        try {
//            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
//            bField.setAccessible(true);
//            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
//            cField.setAccessible(true);
//            bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
//            bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
//            cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
//            cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
//        } catch (Exception exc) {
//            exc.printStackTrace();
//        }
//
//        Core.explosiveSheep.add(this);

        new SheepColorRunnable(7, true, s, this, player);
    }

    class SheepColorRunnable extends BukkitRunnable {
        private boolean red;
        private double time;
        private Sheep s;
        private ExplosiveSheep gadgetExplosiveSheep;
        private Player player;

        public SheepColorRunnable(double time, boolean red, Sheep s, ExplosiveSheep gadgetExplosiveSheep, Player player) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.runTaskLater(RebornCore.getRebornCore(), (int) time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
            this.player = player;
        }

        @Override
        public void run() {
            if (red) {
                s.setColor(DyeColor.RED);
            } else {
                s.setColor(DyeColor.WHITE);
            }
            s.getWorld().playSound(s.getLocation(), Sound.CLICK, 5, 1);
            red = !red;
            time -= 0.2;

            if (time < 0.5) {
                s.getWorld().playSound(s.getLocation(), Sound.EXPLODE, 2, 1);
                s.getWorld().spigot().playEffect(s.getLocation(), Effect.EXPLOSION_HUGE);
                for (int i = 0; i < 50; i++) {
                    final Sheep sheep = player.getWorld().spawn(s.getLocation(), Sheep.class);
                    try {
                        sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                    } catch (Exception exc) {
                    }
                    Random r = new Random();
                    MathUtils.applyVelocity(sheep, new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                    sheep.setBaby();
                    sheep.setAgeLock(true);
                    sheep.setNoDamageTicks(120);
//                    EntitySheep entitySheep = ((CraftSheep) sheep).getHandle();
//
//                    try {
//                        Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
//                        bField.setAccessible(true);
//                        Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
//                        cField.setAccessible(true);
//                        bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
//                        bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
//                        cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
//                        cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
//
//
//                        entitySheep.goalSelector.a(3, new CustomPathFinderGoalPanic(entitySheep, 0.4d));
//
//                        entitySheep.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.4D);
//
//                    } catch (Exception exc) {
//                        exc.printStackTrace();
//                    }
                    Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
                        @Override
                        public void run() {
                            sheep.getWorld().spigot().playEffect(sheep.getLocation(), Effect.LAVA_POP, 0, 0, 0, 0, 0, 0, 5, 32);
                            sheep.remove();
                            //Core.explosiveSheep.remove(gadgetExplosiveSheep);
                        }
                    }, 110);
                }
                sheepArrayList.remove(s);
                s.remove();
                cancel();
            } else {
                Bukkit.getScheduler().cancelTask(getTaskId());
                new SheepColorRunnable(time, red, s, gadgetExplosiveSheep, player);
            }
        }

    }
}
