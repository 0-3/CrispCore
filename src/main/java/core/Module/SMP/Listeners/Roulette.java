package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class Roulette implements Listener {
    boolean isRunning = false;
    HashMap<UUID, UUID> itemPickUps = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME) || isRunning)
            return;

        Location blockBelow = event.getClickedBlock().getLocation().clone().add(0, -1, 0);
        if (!blockBelow.getBlock().getType().toString().contains("SIGN"))
            return;

        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());

        if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Roulette is coming soon");
            return;
        }

        if (rebornPlayer.getBalance("SMP") < 100) {
            event.getPlayer().sendMessage(ChatColor.RED + "You need $100 to run the roulette");
            return;
        }
        rebornPlayer.takeBalance("SMP", 100, true, ChatColor.RED + "" + ChatColor.BOLD + "- $%amount%");

        isRunning = true;
        Location location = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);

		/*ShulkerBullet shulkerBullet = (ShulkerBullet) location.getWorld().spawnEntity(location, EntityType.SHULKER_BULLET);
        shulkerBullet.setInvulnerable(true);
		shulkerBullet.setTarget(shulkerBullet);

		Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
			shulkerBullet.remove();
//			armorStand.remove();
		}, 10L);*/
        Player player = event.getPlayer();
        Location location1 = location.clone().add(0, 1, 0);
        final int[] i = {0};
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
				/*ShulkerBullet shulkerBullet = (ShulkerBullet) location.getWorld().spawnEntity(location1, EntityType.SHULKER_BULLET);
                shulkerBullet.setInvulnerable(true);
				shulkerBullet.setTarget(shulkerBullet);
				Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), shulkerBullet::remove, 2L);*/

                if (i[0] < 18) {
                    //1.9//location1.getWorld().spawnParticle(Particle.PORTAL, location1, 8);
					/*1.8*/
                    location1.getWorld().playEffect(location1, Effect.PORTAL, 8);
                }

                i[0]++;
                if (i[0] == 20 * 2) {
                    Location location2 = location1.clone().add(0, -4, 0);

                    ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location2, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
/*
					ShulkerBullet shulkerBullet1 = (ShulkerBullet) location.getWorld().spawnEntity(location1, EntityType.SHULKER_BULLET);
					shulkerBullet1.setInvulnerable(true);
					shulkerBullet1.setTarget(armorStand);*/
                    Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
                        armorStand.remove();
                        if (player.isOnline()) {
                            //player.playSound(location2, Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1);
                            player.playSound(location2, Sound.ENDERDRAGON_GROWL, 1, 1);

                            int rand = OtherUtil.randInt(1, 3);
                            Item item = null;
                            switch (rand) {
                                default:
                                case 1:
                                    item = location1.getWorld().dropItem(location1, new ItemStack(Material.EMERALD));
                                    break;
                                case 2:
                                    item = location1.getWorld().dropItem(location1, new ItemStack(Material.DIAMOND));
                                    break;
                                case 3:
                                    item = location1.getWorld().dropItem(location1, new ItemStack(Material.GOLD_INGOT));
                                    break;
                            }
                            item.setVelocity(new Vector(0, 0, 0));
                            itemPickUps.put(item.getUniqueId(), player.getUniqueId());

                        }
                    }, 10L);
                    isRunning = false;
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(RebornCore.getRebornCore(), 10L, 2L);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!itemPickUps.containsKey(event.getItem().getUniqueId()))
            return;
        if (!event.getPlayer().getUniqueId().equals(itemPickUps.get(event.getItem().getUniqueId())))
            event.setCancelled(true);
    }

}
