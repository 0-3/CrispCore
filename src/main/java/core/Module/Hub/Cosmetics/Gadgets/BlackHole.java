package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.ItemFactory;
import network.reborn.core.Util.MathUtils;
import network.reborn.core.Util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class BlackHole extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    Item i;

    public BlackHole() {
        super("Black Hole", "black-hole", Material.STAINED_CLAY, 0);
        setData((short) 15);
    }

    @Override
    public void doGadget(final Player player) {
        if (i != null) {
            i.remove();
            i = null;
        }
        Item item = player.getWorld().dropItem(player.getEyeLocation(), ItemFactory.create(Material.STAINED_CLAY, (byte) 0xf, UUID.randomUUID().toString()));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(player.getEyeLocation().getDirection().multiply(1.3d));
        i = item;
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                if (i != null && i.isOnGround()) {
                    int strands = 6;
                    int particles = 25;
                    float radius = 5;
                    float curve = 10;
                    double rotation = Math.PI / 4;

                    Location location = i.getLocation();
                    for (int i = 1; i <= strands; i++) {
                        for (int j = 1; j <= particles; j++) {
                            float ratio = (float) j / particles;
                            double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                            double x = Math.cos(angle) * ratio * radius;
                            double z = Math.sin(angle) * ratio * radius;
                            location.add(x, 0, z);
                            UtilParticles.play(location, Effect.LARGE_SMOKE);
                            location.subtract(x, 0, z);
                        }
                    }
                    for (Entity ent : i.getNearbyEntities(5, 3, 5)) {
                        Vector vector = i.getLocation().toVector().subtract(ent.getLocation().toVector());
                        MathUtils.applyVelocity(ent, vector);
                        if (ent instanceof Player)
                            ((Player) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 40));
                    }
                }
                if (i != null) {
                    i.remove();
                    i = null;
                }
            }
        }, 140);
    }

}
