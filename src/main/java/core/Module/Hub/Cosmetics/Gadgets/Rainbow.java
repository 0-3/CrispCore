package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.DirectionUtils;
import network.reborn.core.Util.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rainbow extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    private List<Entity> entities = new ArrayList<Entity>();

    public Rainbow() {
        super("Rainbow", "rainbow", Material.WOOL, 0);
    }

    @Override
    public void doGadget(final Player player) {
        entities = createRainbow(player.getLocation(), DirectionUtils.getCardinalDirection(player));
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Entity entity : entities) {
                    if (!entity.isDead()) entity.remove();
                }
            }

        }.runTaskLater(RebornCore.getRebornCore(), 100L);
    }

    private List<Entity> createRainbow(final Location location, final DirectionUtils.Direction direction) {
        final Location copy = location.clone();
        final List<Entity> entities = new ArrayList<Entity>();
        @SuppressWarnings("deprecation")
        final byte[] colors = {DyeColor.PINK.getWoolData(), DyeColor.PURPLE.getWoolData(), DyeColor.LIGHT_BLUE.getWoolData(), DyeColor.LIME.getWoolData(), DyeColor.YELLOW.getWoolData(), DyeColor.ORANGE.getWoolData(), DyeColor.RED.getWoolData()};
        byte[] array;
        for (int length = (array = colors).length, i = 0; i < length; ++i) {
            final byte color = array[i];
            this.createRainbowLine(copy.add(0.0, 0.37, 0.0), Material.WOOL, color, entities, direction);
        }
        return entities;
    }

    private void createRainbowLine(final Location location, final Material material, final short durability, final List<Entity> entities, final DirectionUtils.Direction direction) {
        final Location copy = location.clone();
        this.createRainbowPart(copy, material, durability, 2, 4, entities, direction);
        this.createRainbowPart(copy, material, durability, 3, 2, entities, direction);
        this.createRainbowPart(copy, material, durability, 4, 1, entities, direction);
        this.createRainbowPart(copy, material, durability, 10, 1, entities, direction);
        this.createRainbowPart(copy, material, durability, 1, 1, entities, direction);
        this.createRainbowPart(copy, material, durability, 10, 1, false, entities, direction);
        this.createRainbowPart(copy, material, durability, 4, 1, false, entities, direction);
        this.createRainbowPart(copy, material, durability, 3, 2, false, entities, direction);
        this.createRainbowPart(copy, material, durability, 2, 4, false, entities, direction);
    }

    private void createRainbowPart(final Location location, final Material material, final short durability, final int items, final int lines, final List<Entity> entities, final DirectionUtils.Direction direction) {
        this.createRainbowPart(location, material, durability, items, lines, true, entities, direction);
    }

    private void createRainbowPart(Location location, final Material material, final short durability, final int items, final int lines, final boolean add, final List<Entity> entities, final DirectionUtils.Direction direction) {
        final World world = location.getWorld();
        for (int i = 0; i < lines; ++i) {
            location.add(0.0, (add ? 1 : -1) * 0.9, 0.0);
            for (int j = 0; j < items; ++j) {
                location = location.add(((direction == DirectionUtils.Direction.EAST) ? 1 : -1) * -0.37, 0.0, ((direction == DirectionUtils.Direction.NORTH) ? 1 : -1) * -0.37);
                @SuppressWarnings({"unchecked", "rawtypes"})
                final WitherSkull skull = (WitherSkull) world.spawn(location, (Class) WitherSkull.class);
                skull.setDirection(new Vector(0, 0, 0));
                skull.setVelocity(new Vector(0, 0, 0));
                final Item item = world.dropItem(location, ItemBuilder.getRandomResource(material, durability));
                item.setPickupDelay(Integer.MAX_VALUE);
                item.setVelocity(new Vector(0, 0, 0));
                skull.setPassenger(item);
                entities.add(skull);
                entities.add(item);
            }
        }
    }

}
