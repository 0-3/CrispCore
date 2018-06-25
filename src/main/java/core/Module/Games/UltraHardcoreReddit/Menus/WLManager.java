package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
import network.reborn.core.Util.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by ethan on 1/7/2017.
 */
public class WLManager implements Listener {

    ArrayList<Player> ignore = new ArrayList<>();

    public static void openMenu(Player p) {
        GUI gui = new GUI("Whitelist Manager");
        //row 1
        gui.addItem(GameMenu.ni(3));
        gui.addItem(GameMenu.constructMenuItem(Material.PAPER, 0, ChatColor.YELLOW + "Whitelist: " + GameMenu.colorFromBool(Bukkit.hasWhitelist()) + GameMenu.nameFromBool(Bukkit.hasWhitelist()), ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Click to toggle Whitelist"));
        gui.addItem(GameMenu.ni(5));
        gui.addItem(GameMenu.constructMenuItem(Material.WOOL, 5, ChatColor.GREEN + "ADD PLAYER", "Add player to Whitelist"));
        gui.addItem(GameMenu.ni(1));
        gui.addItem(GameMenu.constructMenuItem(Material.WOOL, 14, ChatColor.RED + "REMOVE PLAYER", "Remove player from Whitelist"));
        gui.addItem(GameMenu.ni(4));
        gui.addItem(GameMenu.constructMenuItem(Material.EMERALD_BLOCK, 0, ChatColor.DARK_GREEN + "ADD ALL PLAYERS", "Add all online players to the Whitelist"));
        gui.addItem(GameMenu.ni(1));
        gui.addItem(GameMenu.constructMenuItem(Material.REDSTONE_BLOCK, 0, ChatColor.DARK_RED + "CLEAR WHITELIST", "Empties the Whitelist of all players"));

        Inventory i = gui.create(1, false);
        i.remove(Material.GLASS);
        p.openInventory(i);
    }

    static Boolean isWhitelisted(String name) {
        return Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(name));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getTitle() == null) {
            return;
        }

        if (event.getView().getTopInventory() == null) {
            return;
        }

        if (event.getView().getTopInventory().getTitle() != null) {
            if (event.getView().getTopInventory().getTitle().contains("Whitelist Manager")) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
                Inventory i = event.getClickedInventory();
                if (i.getItem(event.getSlot()) != null && i.getItem(event.getSlot()) instanceof ItemStack) {
                    ItemStack item = i.getItem(event.getSlot());
                    ItemMeta m = item.getItemMeta();
                    if (m.hasDisplayName()) {
                        String s = m.getDisplayName();
                        Player p = (Player) event.getWhoClicked();
                        if (s.contains("Whitelist:")) {
                            if (Bukkit.hasWhitelist()) {
                                Bukkit.setWhitelist(false);
                            } else {
                                Bukkit.setWhitelist(true);
                            }
                            UltraHardcoreReddit.broadcastUHCMessage(ChatColor.YELLOW + "Whitelist has been set to " + GameMenu.colorFromBool(Bukkit.hasWhitelist()) + GameMenu.nameFromBool(Bukkit.hasWhitelist()) + ChatColor.YELLOW + " by " + ChatColor.AQUA + event.getWhoClicked().getName());
                            ignore.add(p);
                            openMenu(p);
                        } else if (s.contains("ADD PLAYER")) {
                            SignGUI getPlayer = new SignGUI(RebornCore.getRebornCore());
                            ArrayList<String> def = new ArrayList<>();
                            def.add(0, "");
                            def.add(1, "^^^^^^^^^");
                            def.add(2, "Player Name");
                            def.add(3, "<ADD TO WL>");
                            getPlayer.open(p, def.toArray(new String[def.size()]), new SignGUI.SignGUIListener() {
                                @Override
                                public void onSignDone(Player player, String[] lines) {
                                    String s = ChatColor.stripColor(lines[0].replaceAll("\"", "").replaceAll("\'", ""));
                                    if (s == null || s.length() == 0) {
                                        UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Please enter a player name.", player);
                                        return;
                                    }
                                    if (s.length() >= 1) {
                                        if (Bukkit.getOfflinePlayer(s) == null || !(Bukkit.getOfflinePlayer(s) instanceof OfflinePlayer)) {
                                            UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "That player does not appear to have ever logged in. Attempting manual adding.", player);
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + s);
                                        } else {
                                            Bukkit.getLogger().info("Adding \"" + s + "\"");
                                            Bukkit.getOfflinePlayer(s).setWhitelisted(true);
                                        }
                                        UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + s + ChatColor.YELLOW + " has been added to the Whitelist by " + ChatColor.AQUA + player.getName());
                                    }
                                }
                            });
                        } else if (s.contains("REMOVE PLAYER")) {
                            SignGUI getPlayer = new SignGUI(RebornCore.getRebornCore());
                            ArrayList<String> def = new ArrayList<>();
                            def.add(0, "");
                            def.add(1, "^^^^^^^^^");
                            def.add(2, "Player Name");
                            def.add(3, "<REM FRM WL>");
                            getPlayer.open(p, def.toArray(new String[def.size()]), new SignGUI.SignGUIListener() {
                                @Override
                                public void onSignDone(Player player, String[] lines) {
                                    String s = ChatColor.stripColor(lines[0].replaceAll("\"", "").replaceAll("\'", ""));
                                    if (s == null || s.length() == 0) {
                                        UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Please enter a player name.", player);
                                        return;
                                    }
                                    if (s.length() >= 1) {
                                        if (Bukkit.getOfflinePlayer(s) == null || !(Bukkit.getOfflinePlayer(s) instanceof OfflinePlayer)) {
                                            UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "That player does not appear to have ever logged in. Attempting manual removal.", player);
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + s);
                                        } else {
                                            Bukkit.getLogger().info("Removing \"" + s + "\"");
                                            Bukkit.getOfflinePlayer(s).setWhitelisted(false);
                                            if (Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s) instanceof Player) {
                                                UltraHardcoreReddit.sendUHCMessage(ChatColor.DARK_RED + "You have been kicked from the game!", Bukkit.getPlayer(s));
                                                RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(s)).sendToRandomHub();

                                            }
                                        }
                                        UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + s + ChatColor.YELLOW + " has been removed from the Whitelist by " + ChatColor.AQUA + player.getName());
                                    }
                                }
                            });
                        } else if (s.contains("ADD ALL")) {
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                pl.setWhitelisted(true);
                            }
                            UltraHardcoreReddit.broadcastUHCMessage(ChatColor.YELLOW + "All players have been added to the Whitelist by " + ChatColor.AQUA + event.getWhoClicked().getName());
                        } else if (s.contains("CLEAR WHITELIST")) {
                            for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()) {
                                op.setWhitelisted(false);
                            }
                            UltraHardcoreReddit.broadcastUHCMessage(ChatColor.YELLOW + "Whitelist has been cleared by " + ChatColor.AQUA + event.getWhoClicked().getName());

                        }
                        ((Player) event.getWhoClicked()).updateInventory();
                    }
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
                if (event.getView().getTopInventory().getTitle().contains("Whitelist Manager")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), new Runnable() {
                        @Override
                        public void run() {
                            GameMenu.openMenu(p);
                        }
                    }, 5L);

                }
            }
        }
    }

}
