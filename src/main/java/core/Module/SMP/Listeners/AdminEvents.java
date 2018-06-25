package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Module.SMP.Commands.AdminCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdminEvents implements Listener {

    public static HashMap<UUID, Chest> chestStorage = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> chestBackup = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> tempChestBackup = new HashMap<>();

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (AdminCommand.admins.contains(event.getPlayer().getUniqueId())) {
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                return;
            }
            Block block = event.getClickedBlock();
            if (block.getType().toString().contains("CHEST")) {
                if (block.getType().equals(Material.ENDER_CHEST)) {
                    //TODO:
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                    if (block.getState() instanceof Chest) {
                        Chest chest = (Chest) block.getState();
                        Inventory inventory = Bukkit.createInventory(null, chest.getInventory().getSize(), chest.getInventory().getName() + " (ADMIN MODE)");
                        inventory.setContents(chest.getInventory().getContents());
                        tempChestBackup.put(event.getPlayer().getUniqueId(), chest.getInventory().getContents());
                        chestStorage.put(event.getPlayer().getUniqueId(), chest);
                        int slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9 = 0;
                        slot1 = 18;
                        slot2 = 19;
                        slot3 = 20;
                        slot4 = 21;
                        slot5 = 22;
                        slot6 = 23;
                        slot7 = 24;
                        slot8 = 25;
                        slot9 = 26;

                        List<String> lore = new ArrayList<>();
                        ItemStack cancel = new ItemStack(Material.WOOL, 1, (short) 14);
                        ItemMeta cancelMeta = cancel.getItemMeta();
                        cancelMeta.setDisplayName(ChatColor.DARK_RED + "Cancel");
                        lore = new ArrayList<>();
                        lore.add("cancel");
                        cancelMeta.setLore(lore);
                        cancel.setItemMeta(cancelMeta);

                        ItemStack save = new ItemStack(Material.WOOL, 1, (short) 5);
                        ItemMeta saveMeta = save.getItemMeta();
                        saveMeta.setDisplayName(ChatColor.GREEN + "Save");
                        lore = new ArrayList<>();
                        lore.add("save");
                        saveMeta.setLore(lore);
                        save.setItemMeta(saveMeta);

                        ItemStack edit = new ItemStack(Material.WATER_BUCKET);
                        ItemMeta editMeta = edit.getItemMeta();
                        editMeta.setDisplayName(ChatColor.DARK_RED + "Restore Backup");
                        lore = new ArrayList<>();
                        lore.add("restore");
                        editMeta.setLore(lore);
                        edit.setItemMeta(editMeta);

                        event.getPlayer().getInventory().setItem(slot3, cancel);
                        event.getPlayer().getInventory().setItem(slot5, save);
                        event.getPlayer().getInventory().setItem(slot7, edit);
                        event.getPlayer().openInventory(inventory);
                        // 28 29 30 31 32 34 35 36 37
                        // 55 56 57 58 59 60 61 62 63
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (AdminCommand.admins.contains(event.getWhoClicked().getUniqueId())) {
            if (event.getInventory().getName().contains("ADMIN")) {
                if (event.getClickedInventory().getName().equals("container.inventory")) {
                    switch (event.getCurrentItem().getItemMeta().getLore().get(0).toLowerCase()) {
                        case "save":
                            event.setCancelled(true);
                            chestBackup.put(event.getWhoClicked().getUniqueId(), tempChestBackup.get(event.getWhoClicked().getUniqueId()));
                            chestStorage.get(event.getWhoClicked().getUniqueId()).getInventory().setContents(event.getInventory().getContents());
                            event.getWhoClicked().closeInventory();
                            break;
                        case "cancel":
                            event.setCancelled(true);
                            chestStorage.remove(event.getWhoClicked().getUniqueId());
                            chestBackup.remove(event.getWhoClicked().getUniqueId());
                            event.getWhoClicked().closeInventory();
                            break;
                        case "restore":
                            event.setCancelled(true);
                            chestStorage.get(event.getWhoClicked().getUniqueId()).getInventory().setContents(chestBackup.get(event.getWhoClicked().getUniqueId()));
                            event.getWhoClicked().closeInventory();
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().contains("ADMIN MODE") && AdminCommand.admins.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().setContents(new ItemStack[]{});
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().toUpperCase().contains("CHEST") && AdminCommand.admins.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
