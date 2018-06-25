package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.ItemBuilder;
import network.reborn.core.Util.MathUtils;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyPopper extends Gadget {
    private static boolean doGlass = false; // Used for staff in the future?
    private static boolean doSnow = false; // Used for Christmas
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public PartyPopper() {
        super("Party Popper", "party-popper", doSnow ? Material.SNOW_BALL : Material.STAINED_CLAY, 0);
        setCooldown(30);
    }

    @Override
    public void doGadget(final Player player) {
        final World world = player.getWorld();
        final Integer[] count = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                }
                if (count[0] != 50) {
                    final List<Item> entities = new ArrayList<>();
                    for (int i = 0; i < 3; ++i) {
                        final Location location = player.getLocation().add(0.0, 2.0, 0.0);
                        Item nonFinalItem;
                        if (doGlass) {
                            nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.STAINED_GLASS, (short) MathUtils.random.nextInt(15)));
                        } else if (doSnow) {
                            switch (OtherUtil.randInt(0, 3)) {
                                default:
                                case 0:
                                    nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.SNOW_BLOCK));
                                    break;
                                case 1:
                                    nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.SNOW_BALL));
                                    break;
                                case 2:
                                    nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.ICE));
                                    break;
                                case 3:
                                    nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.PACKED_ICE));
                                    break;
                            }
                        } else {
                            nonFinalItem = world.dropItem(location, ItemBuilder.getRandomResource(Material.STAINED_CLAY, (short) MathUtils.random.nextInt(15)));
                        }
                        final Item item = nonFinalItem;

                        item.setPickupDelay(Integer.MAX_VALUE);
                        item.setVelocity(new Vector(MathUtils.random(-0.1f, 0.1f), 0.4f, MathUtils.random(-0.1f, 0.1f)));
                        world.playSound(location, Sound.CHICKEN_EGG_POP, 2, 1);
                        entities.add(item);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (final Item item : entities) {
                                if (!item.isDead())
                                    item.remove();
                            }
                        }
                    }.runTaskLater(RebornCore.getRebornCore(), 60L);
                } else {
                    this.cancel();
                }
                count[0]++;
            }
        }.runTaskTimer(RebornCore.getRebornCore(), 0L, 5L).getTaskId();
    }

}
