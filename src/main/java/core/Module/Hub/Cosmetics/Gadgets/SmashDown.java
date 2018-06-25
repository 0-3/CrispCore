package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.BlockUtils;
import network.reborn.core.Util.MathUtils;
import network.reborn.core.Util.UtilParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmashDown extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    HashMap<String, Integer> tasks = new HashMap<>();
    HashMap<String, Integer> height = new HashMap<>();
    List<FallingBlock> fallingBlocks = new ArrayList<>();

    public SmashDown() {
        super("Smash Down", "smash-down", Material.FIREWORK_CHARGE, 0);
        setCooldown(30);
    }

    @Override
    public void doGadget(final Player player) {
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 2, 1);
        player.setVelocity(new Vector(0, 3, 0));
        final int taskId = Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                UtilParticles.play(player.getLocation(), Effect.CLOUD);
            }
        }, 0, 1).getTaskId();
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(taskId);
                height.put(player.getName(), player.getLocation().getBlockY());
                player.setVelocity(new Vector(0, -3, 0));
                tasks.put(player.getName(), Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), new Runnable() {
                    @Override
                    public void run() {
                        if (player.isOnGround() && tasks.containsKey(player.getName())) {
                            Bukkit.getScheduler().cancelTask(tasks.get(player.getName()));
                            int radius = 5;
                            if (player.isSneaking()) // TODO Depend on network level/rank maybe? See if it gets abused first lmao
                                radius = 10;
                            int diff = height.get(player.getName()) - player.getLocation().getBlockY() - 38;
                            if (diff > 0)
                                radius = radius + (diff / 2);
                            if (radius > 25)
                                radius = 25;
                            playBoomEffect(player, radius);
                            tasks.remove(player.getName());
                        }
                    }
                }, 5l, 5l).getTaskId());
            }
        }, 25);
    }

    private void playBoomEffect(final Player player, final int blockRadius) {
        final Location loc = player.getLocation();
        if (!player.isOnGround())
            return;
        loc.getWorld().playSound(loc, Sound.EXPLODE, 2, 1);
        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                if (i == blockRadius) {
                    cancel();
                }
                for (Block b : BlockUtils.getBlocksInRadius(loc.clone().add(0, -1, 0), i, true)) {
                    if (b.getLocation().getBlockY() == loc.getBlockY() - 1) {
                        if (b.getType() != Material.AIR
                                && b.getType() != Material.SIGN_POST
                                && b.getType() != Material.CHEST
                                && b.getType() != Material.STONE_PLATE
                                && b.getType() != Material.WOOD_PLATE
                                && b.getType() != Material.WALL_SIGN
                                && b.getType() != Material.WALL_BANNER
                                && b.getType() != Material.STANDING_BANNER
                                && b.getType() != Material.CROPS
                                && b.getType() != Material.LONG_GRASS
                                && b.getType() != Material.SAPLING
                                && b.getType() != Material.DEAD_BUSH
                                && b.getType() != Material.RED_ROSE
                                && b.getType() != Material.RED_MUSHROOM
                                && b.getType() != Material.BROWN_MUSHROOM
                                && b.getType() != Material.TORCH
                                && b.getType() != Material.LADDER
                                && b.getType() != Material.VINE
                                && b.getType() != Material.DOUBLE_PLANT
                                && b.getType() != Material.PORTAL
                                && b.getType() != Material.CACTUS
                                && b.getType() != Material.WATER
                                && b.getType() != Material.STATIONARY_WATER
                                && b.getType() != Material.LAVA
                                && b.getType() != Material.STATIONARY_LAVA
                                && !BlockUtils.isRocketBlock(b)
                                && b.getType().isSolid()
                                && b.getType().getId() != 43
                                && b.getType().getId() != 44
                                && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation().clone().add(0, 1.1f, 0), b.getType(), b.getData());
//                            fb.setVelocity(new Vector(0, 0.3f, 0));
                            fb.setVelocity(new Vector(0, 0.4f, 0));
                            fb.setDropItem(false);
                            fallingBlocks.add(fb);
                            for (Entity ent : fb.getNearbyEntities(1, 1, 1)) {
                                if (ent != player && ent.getType() != EntityType.FALLING_BLOCK)
                                    if (true) // affect players
                                        MathUtils.applyVelocity(ent, new Vector(0, .9, 0));
                            }
                        }
                    }
                }
                i++;
            }
        }.runTaskTimer(RebornCore.getRebornCore(), 0, 1);
    }

    @EventHandler
    public void onBlockChangeState(EntityChangeBlockEvent event) {
        if (fallingBlocks.contains(event.getEntity())) {
            event.setCancelled(true);
            fallingBlocks.remove(event.getEntity());
            FallingBlock fb = (FallingBlock) event.getEntity();
            fb.getWorld().spigot().playEffect(fb.getLocation(), Effect.STEP_SOUND, fb.getBlockId(), (int) fb.getBlockData(), 0, 0, 0, 0, 1, 32);
            event.getEntity().remove();
        }
    }

}
