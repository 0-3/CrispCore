package network.reborn.core.Module.Games.UltraHardcoreReddit.Scenarios;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by ethan on 12/16/2016.
 */
@Scenario(
        name = "TimeBomb",
        enableMethod = "onEnable",
        disableMethod = "onDisable"
)
public class TimeBomb implements Listener {

    public static void onEnable() {
        Bukkit.getLogger().info("TimeBomb: ON");
    }

    public static void onDisable() {
        Bukkit.getLogger().info("TimeBomb: OFF");
    }

    public static ItemStack getMenuItem() {
        ItemStack i = new ItemStack(Material.TNT);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.WHITE + "TimeBomb");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.YELLOW + "30 seconds after a player dies,");
        lore.add(ChatColor.YELLOW + "a TNT bomb goes off at the");
        lore.add(ChatColor.YELLOW + "death location.");
        im.setLore(lore);
        i.setItemMeta(im);
        return i;
    }
}
