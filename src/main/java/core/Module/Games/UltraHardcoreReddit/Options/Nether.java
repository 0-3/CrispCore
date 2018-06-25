package network.reborn.core.Module.Games.UltraHardcoreReddit.Options;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOption;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ethan on 1/13/2017.
 */
@UHCOption(
        name = "Nether",
        enableMethod = "onEnable",
        disableMethod = "onDisable",
        itemMethod = "getMenuItem"

)
public class Nether implements Listener {

    static Boolean enabled = false;

    public static void onEnable() {
        enabled = true;
    }

    public static void onDisable() {
        enabled = false;
    }

    public static ItemStack getMenuItem() {
        return GameMenu.constructMenuItem(Material.OBSIDIAN, 0, ChatColor.WHITE + "Nether", "Players can create nether", "portals and enter the", "Nether.");
    }

    public static Boolean isEnabled() {
        return enabled;
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (!isEnabled()) {
            for (Block b : event.getBlocks()) {
                if (b.getType().equals(Material.OBSIDIAN)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUsePortal(PlayerPortalEvent event) {
        if (!isEnabled()) {
            if (event.getTo().getBlock().getBiome().equals(Biome.HELL)) {
                event.setCancelled(true);
                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Nether is disabled.", event.getPlayer());
            }
        }
    }



}
