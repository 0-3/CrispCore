package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOptionsAPI;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by ethan on 2/6/2017.
 */
public class UHCMenu implements Listener {

    ArrayList<Player> ignore = new ArrayList<>();

    public static void getUHCMainMenu(Player p) {
        Inventory i = Bukkit.createInventory(p, 9, "UHC Info");
        ItemStack scenarios = GameMenu.constructMenuItem(Material.COMMAND, 0, ChatColor.GREEN + "Scenarios", "View enabled scenarios", "and their info.");
        ItemStack info = GameMenu.constructMenuItem(Material.GOLDEN_APPLE, 0, ChatColor.RED + "UHC Configuration", "View set game options", "(Nether, Potions, etc)");
        i.setItem(3, scenarios);
        i.setItem(5, info);
        p.openInventory(i);
    }

    public static void getUHCScenariosMenu(Player p) {
        ScenariosMenu.getDisplayGUI(p);
    }

    public static void getUHCInfoMenu(Player p) {
        GUI gui = new GUI("UHC Configuration");
        gui.addItem(GameMenu.constructMenuItem(Material.GOLDEN_APPLE, 0, ChatColor.WHITE + "Golden Food", "Golden Heads: " + onoff(UHCOptionsAPI.isOptionEnabled("Golden Heads")), "Golden Apples Heal: " + ChatColor.YELLOW + "Vanilla"));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.NETHERRACK, 0, ChatColor.WHITE + "Nether", "Nether Portal Forming: " + onoff(UHCOptionsAPI.isOptionEnabled("Nether"))));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.POTION, 0, ChatColor.WHITE + "Potions", "Potion Use: " + onoff(UHCOptionsAPI.isOptionEnabled("Potions")), "Potion Brewing: " + onoff(UHCOptionsAPI.isOptionEnabled("Potions")), "Tier II Potions: " + ChatColor.RED + "Off"));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.MONSTER_EGG, 100, ChatColor.WHITE + "Horses", "Horse Riding: " + ChatColor.GREEN + "On", "Horse Armor: " + ChatColor.GREEN + "On"));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.ENDER_PORTAL_FRAME, 0, ChatColor.WHITE + "The End", "End Portal Forming: " + ChatColor.RED + "Off", "EnderDragon: " + ChatColor.RED + "Off"));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.GRASS, 0, ChatColor.WHITE + "World Settings", "Difficulty: " + ChatColor.YELLOW + "Hard", "Map Generation: " + ChatColor.YELLOW + "Vanilla", "Ore Calculations: " + ChatColor.YELLOW + "Vanilla"));
        gui.addItem(new ItemStack(Material.GLASS));
        gui.addItem(GameMenu.constructMenuItem(Material.MOB_SPAWNER, 0, ChatColor.WHITE + "Mob Settings", "Monster Spawning: " + ChatColor.YELLOW + "Vanilla", "Animal Spawning: " + ChatColor.YELLOW + "Vanilla"));


        Inventory i = gui.create(1, false);
        i.remove(Material.GLASS);
        p.openInventory(i);
    }

    static String onoff(Boolean b) {
        if (b) {
            return ChatColor.GREEN + "On";
        } else {
            return ChatColor.RED + "Off";
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() == null) {
            return;
        }
        if (event.getView().getTopInventory().getTitle() != null) {
            if (event.getView().getTopInventory().getTitle().contains("UHC")) {
                Inventory i = event.getClickedInventory();
                if (i.getTitle().contains("UHC")) {
                    if (i.getTitle().contains("UHC Info")) {
                        if (event.getSlot() == 3) {
                            getUHCScenariosMenu((Player) event.getWhoClicked());
                        } else if (event.getSlot() == 5) {
                            getUHCInfoMenu((Player) event.getWhoClicked());
                        }
                    }
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (ignore.contains(p)) {
            ignore.remove(p);
            return;
        }
        if (event.getView().getTopInventory() != null) {
            if (event.getView().getTopInventory().getTitle() != null) {
                String title = event.getView().getTopInventory().getTitle();
                if (title.contains("Scenarios") && !title.contains("Manager")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> getUHCMainMenu(p), 5L);
                } else if (title.contains("UHC Configuration")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> getUHCMainMenu(p), 5L);
                }
            }
        }
    }


}
