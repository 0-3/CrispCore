package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCExceptions;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOptionsAPI;
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
 * Created by ethan on 1/13/2017.
 */
public class UHCManager implements Listener {

    ArrayList<Player> ignore = new ArrayList<>();


    public static void getManagerGUI(Player p) {
        GUI gui = new GUI("UHC Options Manager");
        for (String s : UHCOptionsAPI.options.keySet()) {
            Class<?> option = UHCOptionsAPI.options.get(s);
            try {
                Object scen = option.newInstance();
                ItemStack item = (ItemStack) option.getMethod("getMenuItem").invoke(scen);
                if (UHCOptionsAPI.isOptionEnabled(s)) {
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
        GUI gui = new GUI("UHC Options");
        for (String s : UHCOptionsAPI.optionsInstance.keySet()) {
            if (UHCOptionsAPI.isOptionEnabled(s)) {
                Class<?> clazz = UHCOptionsAPI.options.get(s);
                try {
                    Object opt = UHCOptionsAPI.optionsInstance.get(s);
                    ItemStack item = (ItemStack) clazz.getMethod("getMenuItem").invoke(opt);
                    gui.addItem(item);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        p.openInventory(gui.create(1, false));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() == null) {
            return;
        }
        if (event.getView().getTopInventory().getTitle() != null) {
            if (event.getView().getTopInventory().getTitle().contains("Options")) {
                Inventory i = event.getClickedInventory();
                ItemStack item = i.getItem(event.getSlot());
                if (i.getTitle().contains("Manager")) {
                    if (item.hasItemMeta() && item.getItemMeta().hasLore() && !item.getItemMeta().getLore().isEmpty() && item.getItemMeta().getLore().contains(ChatColor.GREEN + "ENABLED")) {
                        try {
                            UHCOptionsAPI.disableOption(item.getItemMeta().getDisplayName().replace(ChatColor.BLACK + "", ""), event.getWhoClicked().getName());
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
                            UHCOptionsAPI.enableOption(item.getItemMeta().getDisplayName().replace(ChatColor.BLACK + "", ""), event.getWhoClicked().getName());
                            ignore.add(((Player) event.getWhoClicked()));
                            getManagerGUI((Player) event.getWhoClicked());
                        } catch (UHCExceptions.ScenarioDoesntExistException e) {
                            event.getWhoClicked().sendMessage(ChatColor.RED + e.getMessage());
                        } catch (UHCExceptions.ScenarioNotEnabledException e) {
                            e.printStackTrace();
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
                if (event.getView().getTopInventory().getTitle().contains("UHC Options Manager")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> GameMenu.openMenu(p), 5L);

                }
            }
        }
    }

}
