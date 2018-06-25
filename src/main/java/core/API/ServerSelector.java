package network.reborn.core.API;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSelector implements Listener {
    private LinkedHashMap<String, ServerSelectorItem> serverSelectorItems = new LinkedHashMap<>();
    private Inventory serverSelector;

    public ServerSelector() {
        Bukkit.getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public void addItem(ServerSelectorItem serverSelectorItem) {
        serverSelectorItems.put(serverSelectorItem.getTitle(), serverSelectorItem);
    }

    public void setupServerSelector() {
        int itemsCount = 0;

        HashMap<String, ServerSelectorItem> serverSelectorItems = (HashMap<String, ServerSelectorItem>) this.serverSelectorItems.clone();
        for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
            if (entry.getValue().getServerSelectorItemType() == ServerSelectorItemType.DISABLED || entry.getValue().getServerSelectorItemType() == ServerSelectorItemType.STAFF) {
                serverSelectorItems.remove(entry.getKey());
                return;
            }
            itemsCount++;
        }

        switch (itemsCount) {
            default:
                break;
            case 1:
                serverSelector = Bukkit.createInventory(null, 36, "Game Selector");
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet())
                    serverSelector.setItem(22, entry.getValue().getItemStack());
                break;
            case 2:
                serverSelector = Bukkit.createInventory(null, 36, "Game Selector");
                int i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(21, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(23, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
            case 3:
                serverSelector = Bukkit.createInventory(null, 36, "Game Selector");
                i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(20, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(22, entry.getValue().getItemStack());
                            break;
                        case 2:
                            serverSelector.setItem(24, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
            case 5:
                serverSelector = Bukkit.createInventory(null, 54, "Game Selector");
                i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(21, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(23, entry.getValue().getItemStack());
                            break;
                        case 2:
                            serverSelector.setItem(38, entry.getValue().getItemStack());
                            break;
                        case 3:
                            serverSelector.setItem(40, entry.getValue().getItemStack());
                            break;
                        case 4:
                            serverSelector.setItem(42, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
            case 6:
                serverSelector = Bukkit.createInventory(null, 54, "Game Selector");
                i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(20, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(22, entry.getValue().getItemStack());
                            break;
                        case 2:
                            serverSelector.setItem(24, entry.getValue().getItemStack());
                            break;
                        case 3:
                            serverSelector.setItem(38, entry.getValue().getItemStack());
                            break;
                        case 4:
                            serverSelector.setItem(40, entry.getValue().getItemStack());
                            break;
                        case 5:
                            serverSelector.setItem(42, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
            case 7:
                serverSelector = Bukkit.createInventory(null, 54, "Game Selector");
                i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(19, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(21, entry.getValue().getItemStack());
                            break;
                        case 2:
                            serverSelector.setItem(23, entry.getValue().getItemStack());
                            break;
                        case 3:
                            serverSelector.setItem(25, entry.getValue().getItemStack());
                            break;
                        case 4:
                            serverSelector.setItem(38, entry.getValue().getItemStack());
                            break;
                        case 5:
                            serverSelector.setItem(40, entry.getValue().getItemStack());
                            break;
                        case 6:
                            serverSelector.setItem(42, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
            case 4:
            case 8:
                if (itemsCount > 4)
                    serverSelector = Bukkit.createInventory(null, 54, "Game Selector");
                else
                    serverSelector = Bukkit.createInventory(null, 36, "Game Selector");
                i = 0;
                for (Map.Entry<String, ServerSelectorItem> entry : serverSelectorItems.entrySet()) {
                    switch (i) {
                        case 0:
                            serverSelector.setItem(19, entry.getValue().getItemStack());
                            break;
                        case 1:
                            serverSelector.setItem(21, entry.getValue().getItemStack());
                            break;
                        case 2:
                            serverSelector.setItem(23, entry.getValue().getItemStack());
                            break;
                        case 3:
                            serverSelector.setItem(25, entry.getValue().getItemStack());
                            break;
                        case 4:
                            serverSelector.setItem(37, entry.getValue().getItemStack());
                            break;
                        case 5:
                            serverSelector.setItem(39, entry.getValue().getItemStack());
                            break;
                        case 6:
                            serverSelector.setItem(41, entry.getValue().getItemStack());
                            break;
                        case 7:
                            serverSelector.setItem(43, entry.getValue().getItemStack());
                            break;
                    }
                    i++;
                }
                break;
        }

        ItemStack itemStack = new ItemStack(Material.BOOKSHELF);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Hub");
        itemMeta.setLore(OtherUtil.stringToLore("Connect to a hub.", ChatColor.GRAY));
        itemStack.setItemMeta(itemMeta);
        serverSelector.setItem(4, itemStack);

        itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Hangout Lobbies" + ChatColor.GRAY + " (Coming Soon)");
        itemMeta.setLore(OtherUtil.stringToLore("Friends online? Go hangout with them! No friends online? Go make some!", ChatColor.GRAY));
        itemStack.setItemMeta(itemMeta);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner("ElectronicWizard");
        itemStack.setItemMeta(skullMeta);
//        serverSelector.setItem(3, itemStack);

        itemStack = new ItemStack(Material.BARRIER);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.MAGIC + "Something amazing");
        itemMeta.setLore(OtherUtil.stringToLore("This will probably be something cool, but if we're honest we don't even know yet.", ChatColor.GRAY));
        itemStack.setItemMeta(itemMeta);
//        serverSelector.setItem(5, itemStack);
    }

    public Inventory getServerSelector() {
        return serverSelector;
    }

    public void openServerSelector(Player player) {
        player.openInventory(getServerSelector());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() == null || event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        event.setCancelled(true);

        String title = event.getInventory().getTitle();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        switch (title) {
            default:
                break;
            case "Game Selector":
                switch (ChatColor.stripColor(itemName).replaceAll(" \\((.*)\\)", "")) {
                    default:
                        event.getWhoClicked().sendMessage(ChatColor.RED + ChatColor.stripColor(itemName).replaceAll(" \\((.*)\\)", "") + " is coming soon!");
                        event.getWhoClicked().closeInventory();
                        break;
                    case "Hub":
                        event.getWhoClicked().teleport(Hub.spawns.get(network.reborn.core.API.Module.HUB));
                        Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.HUB);
                        event.getWhoClicked().getInventory().setItem(8, null);
                        Hub.syncPlayersBasedOnLobby();
                        event.getWhoClicked().closeInventory();
                        break;
                    case "SkyWars":
                        event.getWhoClicked().teleport(Hub.spawns.get(network.reborn.core.API.Module.SKYWARS));
                        Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.SKYWARS);

                        ItemStack itemStack = new ItemStack(Material.EMERALD);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.GREEN + "SkyWars Shop " + ChatColor.GRAY + "(Right Click)");
                        itemStack.setItemMeta(itemMeta);

                        event.getWhoClicked().getInventory().setItem(8, itemStack);

                        Hub.syncPlayersBasedOnLobby();
                        event.getWhoClicked().closeInventory();
                        break;
                    case "Ultra Hardcore":
                        event.getWhoClicked().teleport(Hub.spawns.get(Module.ULTRA_HARDCORE));
                        Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.ULTRA_HARDCORE);
                        event.getWhoClicked().getInventory().setItem(8, null);
                        Hub.syncPlayersBasedOnLobby();
                        event.getWhoClicked().closeInventory();
                        break;
                    case "Reddit UHC":
                        event.getWhoClicked().teleport(Hub.spawns.get(Module.UHC_REDDIT));
                        Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.UHC_REDDIT);
                        event.getWhoClicked().getInventory().setItem(8, null);
                        Hub.syncPlayersBasedOnLobby();
                        event.getWhoClicked().closeInventory();
                        break;
                    case "FFA":
//						event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Sending you to SMP");
                        event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Sending you to FFA");
                        ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
                        out1.writeUTF("Connect");
                        out1.writeUTF("11");
                        ((Player) event.getWhoClicked()).sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out1.toByteArray());
                        event.getWhoClicked().closeInventory();
                        break;
                    case "SMP":
                        event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Sending you to SMP");
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF("8");
                        ((Player) event.getWhoClicked()).sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out.toByteArray());
                        event.getWhoClicked().closeInventory();
                        break;
                }
                break;
        }
    }

    @EventHandler
    public void onServerClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasLore())
            return;
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        if (item.getItemMeta().getLore().toString().contains("Click to Connect")) {
            String name = item.getItemMeta().getDisplayName();
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                String match = matcher.group(1);
                String server = match.replaceAll("Server #", "");
                event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Sending you to server #" + server);
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                ((Player) event.getWhoClicked()).sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out.toByteArray());
                event.getWhoClicked().closeInventory();
            }
        }
    }

}
