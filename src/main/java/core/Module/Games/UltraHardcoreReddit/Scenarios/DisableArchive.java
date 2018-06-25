package network.reborn.core.Module.Games.UltraHardcoreReddit.Scenarios;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Scenario;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by ethan on 1/12/2017.
 */
@Scenario(
        name = "Disable Archive",
        enableMethod = "onEnable",
        disableMethod = "onDisable"
)
public class DisableArchive implements Listener {

    private static Boolean enabled = false;

    public static void onEnable() {
        enabled = true;
    }

    public static void onDisable() {
        enabled = false;
    }

    public static ItemStack getMenuItem() {
        ItemStack i = new ItemStack(Material.BARRIER);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.WHITE + "Disable Archive");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.YELLOW + "The Game Archive will not");
        lore.add(ChatColor.YELLOW + "record data about this game.");
        im.setLore(lore);
        i.setItemMeta(im);
        return i;
    }

}
