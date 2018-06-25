package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Archive.GameDataMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia.Post;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.github.paperspigot.Title;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ethan on 12/19/2016.
 */
public class GameMenu implements Listener {

    //UUID, lastObject
    HashMap<String, Location> lastLocation = new HashMap<>();
    HashMap<String, ItemStack[]> lastInventory = new HashMap<>();
    HashMap<String, ItemStack[]> lastArmor = new HashMap<>();

    public static void openMenu(Player p) {
        GUI gui = new GUI("Game Menu");

        //Scenarios Manager
        gui.addItem(constructMenuItem(Material.COMMAND, 0, ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Scenarios Manager", "- Toggle UHC Scenarios", "- View enabled Scenarios"));
        gui.addItem(ni(1));
        //Whitelist Manager
        gui.addItem(constructMenuItem(Material.PAPER, 0, ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Whitelist Manager", "- Enable/Disable Whitelist", "- Add all players to Whitelist", "- Clear whitelist", "- Add/Remove individual players"));
        gui.addItem(ni(1));
        //Settings Manager
        gui.addItem(constructMenuItem(Material.REDSTONE_COMPARATOR, 0, ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Game Manager", "- Start/Stop Reddit UHC Game", "- Set slot count", "- Set current Host"));
        gui.addItem(ni(1));
        //UHC Manager
        gui.addItem(constructMenuItem(Material.GOLDEN_APPLE, 0, ChatColor.RED + "" + ChatColor.UNDERLINE + "UHC Options Manager", "- Set UHC Options"));
        gui.addItem(ni(1));
        //Social Media Manager
        gui.addItem(constructMenuItem(Material.BEACON, 0, ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + "Match Posting Manager", "- Post match on r/UHCMatches", "- Advertise match post on Twitter"));
        gui.addItem(ni(1));
        Boolean wr = worldReady();
        Boolean wc = worldExists();
        gui.addItem(constructMenuItem(Material.EMPTY_MAP, 0, ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "World Info", "World Created: " + colorFromBool(wc) + nameFromBool(wc), "World Pregenerated: " + colorFromBool(wr) + nameFromBool(wr), "Border Radius: " + ChatColor.YELLOW + "1500", ChatColor.ITALIC + "Note: Border size cannot be changed currently."));
        gui.addItem(ni(1));
        String state = StringUtils.capitalize(RebornCore.getCoveAPI().getGame().getGameState().name().toLowerCase());
        String host = (GameManager.currentHost.length() >= 1) ? GameManager.currentHost : "None";
        gui.addItem(constructMenuItem(Material.SIGN, 0, ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Game Info", "Game State: " + ChatColor.YELLOW + state, "Slot Count: " + ChatColor.YELLOW + GameManager.slots, "Online Players: " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size(), "Host: " + ChatColor.YELLOW + host));
        Inventory inv = gui.create(1, false);
        Boolean b = (new File(Bukkit.getWorldContainer() + File.separator + "mapgen.bypass").exists());
        Boolean c = postBypassFileExists();
        inv.setItem(40, constructMenuItem(Material.BARRIER, 0, ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "Debug Info", "Ignore Not Generated: " + colorFromBool(b) + StringUtils.capitalize(b.toString().toLowerCase()), "Bypass Match Post Limit: " + colorFromBool(c) + StringUtils.capitalize(c.toString().toLowerCase())));
        inv.setItem(36, constructMenuItem(Material.WATCH, 0, ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Game Archive", "- View past games", "- See who has hosted"));
        inv.setItem(44, constructMenuItem(Material.COMPASS, 0, ChatColor.DARK_AQUA + "Spectate Match", "- Toggle Spectating mode", "- Saves inventory contents"));
        inv.remove(Material.GLASS);
        p.openInventory(inv);
    }

    static Boolean worldExists() {
        return new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "level.dat").exists();
    }

    static Boolean worldReady() {
        return new File(Bukkit.getWorldContainer() + File.separator + "game" + File.separator + "generation.complete").exists();
    }

    static Boolean postBypassFileExists() {
        return new File(Bukkit.getWorldContainer() + File.separator + "post.bypass").exists();
    }

    static Boolean matchPostNull() {
        return (UltraHardcoreReddit.postURL.equalsIgnoreCase(""));
    }

    /**
     * Constructs an ItemStack for Game Menu GUI
     *
     * @param material Material of item
     * @param name     Name of item (supply color)
     * @param lore     Lore of item (default: gray)
     * @return constructed itemstack
     */
    public static ItemStack constructMenuItem(Material material, int durability, String name, String... lore) {
        ItemStack i = new ItemStack(material);
        i.setDurability((short) durability);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        ArrayList<String> l = new ArrayList<>();
        l.add("");
        for (String s : lore) {
            l.add(ChatColor.GRAY + s);
        }
        im.setLore(l);
        i.setItemMeta(im);
        return i;
    }

    static ChatColor colorFromBool(Boolean b) {
        if (b)
            return ChatColor.GREEN;
        else
            return ChatColor.RED;
    }

    static String nameFromBool(Boolean b) {
        if (b)
            return "ON";
        else
            return "OFF";
    }

    public static ItemStack[] ni(int i) {
        ArrayList<ItemStack> r = new ArrayList<>();
        for (int a = 0; a < i; a++) {
            r.add(constructMenuItem(Material.GLASS, 0, "null", "null"));
        }
        return r.toArray(new ItemStack[r.size()]);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getTitle() == null) {
            return;
        }

        if (event.getClickedInventory().getTitle() != null) {
            if (event.getClickedInventory().getTitle().contains("Game Menu")) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
                Player p = (Player) event.getWhoClicked();
                Inventory i = event.getClickedInventory();
                if (i.getItem(event.getSlot()) != null && i.getItem(event.getSlot()) instanceof ItemStack) {
                    ItemStack item = i.getItem(event.getSlot());
                    ItemMeta m = item.getItemMeta();
                    if (m.hasDisplayName()) {
                        String s = m.getDisplayName();
                        if (s.contains("Scenarios")) {
                            ScenariosMenu.getManagerGUI((Player) event.getWhoClicked());
                        } else if (s.contains("Whitelist")) {
                            WLManager.openMenu((Player) event.getWhoClicked());
                        } else if (s.contains("Game Manager")) {
                            GameManager.openMenu((Player) event.getWhoClicked());
                        } else if (s.contains("UHC Options Manager")) {
                            UHCManager.getManagerGUI(((Player) event.getWhoClicked()));
                        } else if (s.contains("Match Posting Manager")) {
                            event.getView().close();
                            if (matchPostNull() || postBypassFileExists()) {
                                Post.init((Player) event.getWhoClicked());
                            } else {
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "There is already a match posted! A new match cannot be posted until the previously posted one has been started. If this message has been shown in error, contact an Admin to override this message.", ((Player) event.getWhoClicked()));
                            }
                        } else if (s.contains("Game Archive")) {
                            event.getView().close();
                            GameDataMenu.openInv(((Player) event.getWhoClicked()), 1);
                        } else if (s.contains("Spectate Match")) {
                            event.getView().close();
                            if (lastLocation.containsKey(p.getUniqueId().toString())) {
                                String u = p.getUniqueId().toString();
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.GRAY + "Restoring your data, please wait.", p);
                                p.teleport(lastLocation.get(u));
                                p.setGameMode(GameMode.SURVIVAL);
                                p.getInventory().setContents(lastInventory.get(u));
                                p.getInventory().setArmorContents(lastArmor.get(u));
                                p.updateInventory();
                                p.sendTitle(new Title(ChatColor.RED + "UHC", ChatColor.GRAY + "Exited Spectator Mode"));
                                lastLocation.remove(u);
                                lastInventory.remove(u);
                                lastArmor.remove(u);
                            } else {
                                String u = p.getUniqueId().toString();
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.GRAY + "Saving your data, please wait.", p);
                                lastLocation.put(u, p.getLocation());
                                lastInventory.put(u, p.getInventory().getContents());
                                lastArmor.put(u, p.getInventory().getArmorContents());
                                p.getInventory().clear();
                                p.setGameMode(GameMode.SPECTATOR);
                                Location l = new Location(p.getLocation().getWorld(), 0.5, 100.0, 0.5);
                                Location t = new Location(l.getWorld(), 0.5, l.getWorld().getHighestBlockYAt(l), 0.5);
                                p.teleport(t);
                                p.sendTitle(new Title(ChatColor.RED + "UHC", ChatColor.GRAY + "Entered Spectator Mode"));
                            }
                        }
                    }
                }
            }
        }
    }


}
