package network.reborn.core.Module.Hub.GUIs;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkyWarsShop implements Listener {

    public SkyWarsShop() {
        Bukkit.getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public void openGUI(Player player) {
        player.openInventory(getGUI(player));
    }

    private Inventory getGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "SkyWars Shop");

        ItemStack soloKits = new ItemStack(Material.BOW);
        ItemMeta soloKitsMeta = soloKits.getItemMeta();
//		soloKitsMeta.setDisplayName(ChatColor.GREEN + "Solo Kits");
        soloKitsMeta.setDisplayName(ChatColor.GREEN + "Kits");
        soloKits.setItemMeta(soloKitsMeta);
        gui.setItem(13, soloKits);
//		gui.setItem(11, soloKits);

//		ItemStack soloPerks = new ItemStack(Material.BLAZE_POWDER);
//		ItemMeta soloPerksMeta = soloPerks.getItemMeta();
//		soloPerksMeta.setDisplayName(ChatColor.GREEN + "Solo Perks");
//		soloPerksMeta.setLore(OtherUtil.stringToLore("Coming Soon", ChatColor.RED));
//		soloPerks.setItemMeta(soloPerksMeta);
//		gui.setItem(20, soloPerks);

//		ItemStack teamKits = new ItemStack(Material.BOW);
//		ItemMeta teamKitsMeta = teamKits.getItemMeta();
//		teamKitsMeta.setDisplayName(ChatColor.GREEN + "Team Kits");
//		teamKitsMeta.setLore(OtherUtil.stringToLore("Coming Soon", ChatColor.RED));
//		teamKits.setItemMeta(teamKitsMeta);
//		gui.setItem(15, teamKits);
//
//		ItemStack teamPerks = new ItemStack(Material.BLAZE_POWDER);
//		ItemMeta termPerksMeta = teamPerks.getItemMeta();
//		termPerksMeta.setDisplayName(ChatColor.GREEN + "Solo Perks");
//		termPerksMeta.setLore(OtherUtil.stringToLore("Coming Soon", ChatColor.RED));
//		teamPerks.setItemMeta(termPerksMeta);
//		gui.setItem(24, teamPerks);


        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        ItemStack balance = new ItemStack(Material.DOUBLE_PLANT);
        ItemMeta balanceMeta = balance.getItemMeta();
        balanceMeta.setDisplayName(ChatColor.GOLD + "Coins Balance");
        List<String> lore = new ArrayList<>();
        lore.clear();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "Current: " + OtherUtil.formatNumber(rebornPlayer.getBalance("skywars")));
        lore.add(" ");
        balanceMeta.setLore(lore);
        balance.setItemMeta(balanceMeta);
        gui.setItem(31, balance);


        return gui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase("SkyWars Shop")) {
            ItemStack itemStack = event.getCurrentItem();
            event.setCancelled(true);
            if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta())
                return;

            switch (ChatColor.stripColor(itemStack.getItemMeta().getDisplayName().toLowerCase())) {
                default:
                    break;
                case "kits":
                case "solo kits":
                    Hub.skyWarsKitManager.openKitGUI((Player) event.getWhoClicked());
                    break;
//				case "team kits":
//					event.getWhoClicked().sendMessage(ChatColor.RED + "Coming Soon");
//					event.getWhoClicked().closeInventory();
//					break;
//				case "solo perks":
//					event.getWhoClicked().sendMessage(ChatColor.RED + "Coming Soon");
//					event.getWhoClicked().closeInventory();
//					break;
//				case "team perks":
//					event.getWhoClicked().sendMessage(ChatColor.RED + "Coming Soon");
//					event.getWhoClicked().closeInventory();
//					break;
            }
        }
    }

}
