package network.reborn.core.Module.Games.UltraHardcoreReddit.Scenarios;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Scenario;
import network.reborn.core.Util.MathUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 12/16/2016.
 */
@Scenario(
        name = "CutClean",
        enableMethod = "onEnable",
        disableMethod = "onDisable",
        getMenuItem = "getMenuItem"
)
public class CutClean implements Listener {

    private static Boolean enabled = false;


    public static void onEnable() {
        enabled = true;
        Bukkit.getLogger().info("CutClean: ON");
    }

    public static void onDisable() {
        enabled = false;
        Bukkit.getLogger().info("CutClean: OFF");
    }

    public static ItemStack getMenuItem() {
        ItemStack i = new ItemStack(Material.FURNACE);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.WHITE + "CutClean");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.YELLOW + "All mobs drop cooked meat,");
        lore.add(ChatColor.YELLOW + "and ores drop smelted.");
        im.setLore(lore);
        i.setItemMeta(im);
        return i;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (enabled && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            Block b = event.getBlock();
            if (b.getType().equals(Material.IRON_ORE)) {
                event.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
                try {
                    playSound(event.getPlayer(), b);
                } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (b.getType().equals(Material.GOLD_ORE)) {
                event.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
                try {
                    playSound(event.getPlayer(), b);
                } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (b.getType().equals(Material.GRAVEL)) {
                event.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FLINT));
                try {
                    playSound(event.getPlayer(), b);
                } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LEAVES_2)) {
                int c = MathUtils.random(1, 100);
                if (c == 1) {
                    event.setCancelled(true);
                    b.setType(Material.AIR);
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
                }
                if (event.getPlayer().getItemInHand().getType().equals(Material.SHEARS)) {
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(b.getType()));
                }
                try {
                    playSound(event.getPlayer(), b);
                } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void playSound(Player p, Block b) throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        List<Entity> ents = p.getNearbyEntities(16.0, 16.0, 16.0);
        ArrayList<Player> np = new ArrayList<>();

        Field f = CraftSound.class.getDeclaredField("sounds");
        f.setAccessible(true);

        String[] sounds = (String[]) f.get(null);
        Method getBlock = CraftBlock.class.getDeclaredMethod("getNMSBlock");
        getBlock.setAccessible(true);
        Object nmsBlock = getBlock.invoke(b);
        net.minecraft.server.v1_8_R3.Block block = (net.minecraft.server.v1_8_R3.Block) nmsBlock;

        if (b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LEAVES_2)) {
            // pl.playSound(l, Sound.DIG_GRASS, 10, 0);
            for (Sound sound : Sound.values()) {
                if (block.stepSound.getBreakSound()
                        .equals(sounds[sound.ordinal()])) {
                    for (Entity e : ents) {
                        if (e.getType().equals(EntityType.PLAYER)) {
                            Player pl = (Player) e;
                            pl.playSound(b.getLocation(), sound, 10, 10);
                        }
                    }
                }
            }
        } else {
            for (Entity e : ents) {
                if (e.getType().equals(EntityType.PLAYER)) {
                    Player pl = (Player) e;
                    pl.playSound(b.getLocation(), Sound.DIG_STONE, 10, 0);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
         /*Checking Entity Type makes sure that it doesn't modify Player Inventory contents!*/
        if (enabled && !event.getEntityType().equals(EntityType.PLAYER)) {
            for (ItemStack drops : event.getDrops()) {
                if (drops.getType().equals(Material.RAW_CHICKEN)) {
                    drops.setType(Material.COOKED_CHICKEN);
                } else if (drops.getType().equals(Material.RAW_BEEF)) {
                    drops.setType(Material.COOKED_BEEF);
                } else if (drops.getType().equals(Material.PORK)) {
                    drops.setType(Material.GRILLED_PORK);
                } else if (drops.getType().equals(Material.RAW_FISH)) {
                    drops.setType(Material.COOKED_FISH);
                } else if (drops.getType().equals(Material.MUTTON)) {
                    drops.setType(Material.GRILLED_PORK);
                }
            }
        }
    }
}
