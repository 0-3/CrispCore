package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import network.reborn.core.Module.Games.UltraHardcoreReddit.ScenariosAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCExceptions;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by ethan on 12/16/2016.
 */
public class ScenariosMenu implements Listener {

    ArrayList<Player> ignore = new ArrayList<>();

    public static void getManagerGUI(Player p) {
        GUI gui = new GUI("Scenarios Manager");
        for (String s : ScenariosAPI.scenarios.keySet()) {
            Class<?> scenario = ScenariosAPI.scenarios.get(s);
            try {
                Object scen = scenario.newInstance();
                ItemStack item = (ItemStack) scenario.getMethod("getMenuItem").invoke(scen);
                if (ScenariosAPI.enabledScenariosInstance.containsKey(s)) {
                    ItemMeta im = item.getItemMeta();
                    ArrayList<String> lore = (ArrayList<String>) im.getLore();
                    lore.add(ChatColor.GREEN + "ENABLED");
                    im.setLore(lore);
                    item.setItemMeta(im);
                    if (item.getType().equals(Material.GOLDEN_APPLE)) {
                        item.setDurability((short) 1);
                    } else {
                        item = addGlow(item);
                    }
                }
                gui.addItem(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        p.openInventory(gui.create(1, false));

    }

    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static void getDisplayGUI(Player p) {
        GUI gui = new GUI("Scenarios");
        for (String s : ScenariosAPI.enabledScenariosInstance.keySet()) {
            Class<?> clazz = ScenariosAPI.scenarios.get(s);
            try {
                Object scen = ScenariosAPI.enabledScenariosInstance.get(s);
                ItemStack item = (ItemStack) clazz.getMethod("getMenuItem").invoke(scen);
                gui.addItem(item);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        Inventory i = gui.create(1, false);
        if (i.getItem(10) == null) {
            ItemStack empty = GameMenu.constructMenuItem(Material.BARRIER, 0, ChatColor.YELLOW + "No Scenarios Enabled", "There don't appear to be", "any Scenarios enabled currently.");
            i.setItem(22, empty);
        }
        p.openInventory(i);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() == null) {
            return;
        }
        if (event.getView().getTopInventory().getTitle() != null) {
            if (event.getView().getTopInventory().getTitle().contains("Scenario")) {
                Inventory i = event.getClickedInventory();
                ItemStack item = i.getItem(event.getSlot());
                if (i.getTitle().contains("Manager")) {
                    if (item.hasItemMeta() && item.getItemMeta().hasLore() && !item.getItemMeta().getLore().isEmpty() && item.getItemMeta().getLore().contains(ChatColor.GREEN + "ENABLED")) {
                        try {
                            ScenariosAPI.disableScenario(item.getItemMeta().getDisplayName().replace(ChatColor.BLACK + "", ""), event.getWhoClicked().getName());
                            ignore.add(((Player) event.getWhoClicked()));
                            getManagerGUI((Player) event.getWhoClicked());
                        } catch (UHCExceptions.ScenarioDoesntExistException e) {
                            event.getWhoClicked().sendMessage(ChatColor.RED + e.getMessage());
                        } catch (UHCExceptions.ScenarioNotEnabledException e) {
                            event.getWhoClicked().sendMessage(ChatColor.RED + e.getMessage());
                        }
                    } else {
                        try {
                            //Bukkit.broadcastMessage("CLICKED ITEM NAME: \"" + item.getItemMeta().getDisplayName().replace(ChatColor.BLACK + "", "") + "\"");
                            ScenariosAPI.enableScenario(item.getItemMeta().getDisplayName().replace(ChatColor.BLACK + "", ""), event.getWhoClicked().getName());
                            ignore.add(((Player) event.getWhoClicked()));
                            getManagerGUI((Player) event.getWhoClicked());
                        } catch (UHCExceptions.ScenarioDoesntExistException e) {
                            event.getWhoClicked().sendMessage(ChatColor.RED + e.getMessage());
                        }
                    }
                }
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
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
                if (event.getView().getTopInventory().getTitle().contains("Scenarios Manager")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> GameMenu.openMenu(p), 5L);
                }
            }
        }
    }


}
