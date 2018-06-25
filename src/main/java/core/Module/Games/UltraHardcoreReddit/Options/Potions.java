package network.reborn.core.Module.Games.UltraHardcoreReddit.Options;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOption;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ethan on 1/13/2017.
 */
@UHCOption(
        name = "Potions",
        enableMethod = "onEnable",
        disableMethod = "onDisable",
        itemMethod = "getMenuItem"

)
public class Potions implements Listener {

    static Boolean enabled = false;

    public static void onEnable() {
        enabled = true;
    }

    public static void onDisable() {
        enabled = false;
    }

    public static ItemStack getMenuItem() {
        return GameMenu.constructMenuItem(Material.POTION, 0, ChatColor.WHITE + "Potions", "Players can brew potions", "in a Brewing Stand, and", "can use Potions dropped", "from Witches.");
    }

    public static Boolean isEnabled() {
        return enabled;
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        if (!isEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        if (!isEnabled()) {
            if (event.getItem().getType().equals(Material.POTION)) {
                event.setCancelled(true);
                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Potions are disabled.", event.getPlayer());
            }
        }
    }


    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.getEntityType().equals(EntityType.SPLASH_POTION) && !isEnabled()) {
            if (event.getEntity().getShooter() instanceof Player) {
                event.setCancelled(true);
                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Potions are disabled.", (Player) event.getEntity().getShooter());
                return;
            }
            if (!(event.getEntity().getShooter() instanceof Witch)) {
                event.setCancelled(true);
            }

        }
    }


}
