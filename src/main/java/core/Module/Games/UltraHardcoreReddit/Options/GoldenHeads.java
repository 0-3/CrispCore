package network.reborn.core.Module.Games.UltraHardcoreReddit.Options;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOption;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by ethan on 1/13/2017.
 */
@UHCOption(
        name = "Golden Heads",
        enableMethod = "onEnable",
        disableMethod = "onDisable",
        itemMethod = "getMenuItem"
)
public class GoldenHeads implements Listener {

    static Boolean enabled = true;

    public static void onEnable() {
        enabled = true;
    }

    public static void onDisable() {
        enabled = false;
    }

    public static ItemStack getMenuItem() {
        return GameMenu.constructMenuItem(Material.GOLDEN_APPLE, 0, ChatColor.WHITE + "Golden Heads", "Players will drop a", "skull on death. This", "can be used instead of", "an apple to craft", "a Golden Head, which", "provides more health", "regeneration than a", "golden apple.");
    }

    public static Boolean isEnabled() {
        return enabled;
    }


    @EventHandler
    public void onGoldenHead(PlayerItemConsumeEvent event) {
        if (isEnabled()) {
            if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                ItemStack i = event.getItem();
                if (i.hasItemMeta()) {
                    if (i.getItemMeta().hasDisplayName()) {
                        if (i.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
                            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 1), true);
                            event.getPlayer().sendMessage(ChatColor.GOLD + "You consumed a Golden Head!");
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (isEnabled()) {
            Location l = event.getEntity().getLocation();
            Location u = new Location(l.getWorld(), l.getX(), l.getY() - 1.0, l.getZ(), l.getPitch(), l.getYaw());
            u.getBlock().setType(Material.NETHER_FENCE);
            l.getBlock().setType(Material.SKULL);
            Skull skull = (Skull) l.getBlock().getState();
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(event.getEntity().getName());
            skull.update(true);
        }
    }


}
