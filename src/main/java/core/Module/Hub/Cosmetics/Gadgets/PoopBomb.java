package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.ItemBuilder;
import network.reborn.core.Util.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PoopBomb extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public PoopBomb() {
        super("Poop Bomb", "poop-bomb", Material.COCOA, 0);
    }

    @Override
    public void doGadget(final Player player) {
        final List<Item> entities = new ArrayList<Item>();
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final Item item = world.dropItem(location, ItemBuilder.getRandomResource(Material.INK_SACK, (short) 3));
        item.setVelocity(location.getDirection().multiply(1));
        item.setPickupDelay(Integer.MAX_VALUE);
        world.playSound(location, Sound.FUSE, 10.0f, 5.0f);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!item.isDead()) {
                    final Location location = item.getLocation();
                    final World world = location.getWorld();
                    item.remove();
                    world.createExplosion(location, 0.0f);
                    for (int i = 0; i < MathUtils.random.nextInt(41) + 40; ++i) {
                        final Item poop = world.dropItem(location, ItemBuilder.getRandomResource(Material.INK_SACK, (short) 3));
                        final float x = -0.8f + (float) (Math.random() * 2.6);
                        final float y = -0.4f + (float) (Math.random() * 1.8);
                        final float z = -0.8f + (float) (Math.random() * 2.6);
                        poop.setVelocity(new Vector(x, y, z));
                        poop.setPickupDelay(Integer.MAX_VALUE);
                        entities.add(poop);
                    }
                    for (int i = 0; i < MathUtils.random.nextInt(21) + 20; ++i) {
                        final Item poop = world.dropItem(location, ItemBuilder.getRandomResource(Material.WOOL, 1, (short) 12));
                        final float x = -0.8f + (float) (Math.random() * 2.6);
                        final float y = -0.4f + (float) (Math.random() * 1.8);
                        final float z = -0.8f + (float) (Math.random() * 2.6);
                        poop.setVelocity(new Vector(x, y, z));
                        poop.setPickupDelay(Integer.MAX_VALUE);
                        entities.add(poop);
                    }
                }
            }
        }.runTaskLater(RebornCore.getRebornCore(), 80L);

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Entity entity : entities) {
                    if (!entity.isDead()) entity.remove();
                }
            }

        }.runTaskLater(RebornCore.getRebornCore(), 120L);
    }

}
