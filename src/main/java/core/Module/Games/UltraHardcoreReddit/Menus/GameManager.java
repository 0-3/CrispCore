package network.reborn.core.Module.Games.UltraHardcoreReddit.Menus;

import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
import network.reborn.core.Util.SignGUI;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;

/**
 * Created by ethan on 1/14/2017.
 */
public class GameManager implements Listener {

    public static String currentHost = "None";
    public static int slots = 80;
    ArrayList<Player> ignore = new ArrayList<>();

    public static void initSettings() {
        currentHost = "None";
        slots = RebornCore.getCoveAPI().getGame().getMaxPlayers();
    }

    public static void openMenu(Player p) {
        GUI gui = new GUI("Game Manager");
        //row 1
        gui.addItem(GameMenu.ni(2));
        if (!RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.WAITING)) {
            gui.addItem(GameMenu.constructMenuItem(Material.BARRIER, 0, ChatColor.DARK_RED + "Game in Progress"));
        } else {
            gui.addItem(GameMenu.constructMenuItem(Material.WOOL, 5, ChatColor.GREEN + "Start Game", ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Click to start Game"));
        }
        gui.addItem(GameMenu.ni(1));
        if (!RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.INGAME)) {
            gui.addItem(GameMenu.constructMenuItem(Material.BARRIER, 0, ChatColor.DARK_RED + "Game not active"));
        } else {
            gui.addItem(GameMenu.constructMenuItem(Material.WOOL, 14, ChatColor.RED + "Stop Game", ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Click to stop game"));
        }
        gui.addItem(GameMenu.ni(2));

        //row2
        gui.addItem(GameMenu.ni(3));
        gui.addItem(GameMenu.constructMenuItem(Material.NAME_TAG, 0, ChatColor.YELLOW + "Set Host", "Set current host", "", "Current Host: " + currentHost));
        gui.addItem(GameMenu.ni(6));
        gui.addItem(GameMenu.constructMenuItem(Material.SKULL_ITEM, 3, ChatColor.GOLD + "Set Slot Count", "Set max amount of game slots", "", "Slots: " + slots));

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
            if (event.getView().getTopInventory().getTitle().contains("Game Manager")) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
                Inventory i = event.getClickedInventory();
                if (i.getItem(event.getSlot()) != null && i.getItem(event.getSlot()) instanceof ItemStack) {
                    ItemStack item = i.getItem(event.getSlot());
                    ItemMeta m = item.getItemMeta();
                    if (m.hasDisplayName()) {
                        String s = m.getDisplayName();
                        Player p = (Player) event.getWhoClicked();
                        if (s.contains("Start Game")) {
                            Bukkit.dispatchCommand(p, "game start");
                            ignore.add(p);
                            openMenu(p);
                        } else if (s.contains("Stop Game")) {
                            Bukkit.dispatchCommand(p, "game end");
                            ignore.add(p);
                            openMenu(p);
                        } else if (s.contains("Set Host")) {
                            if (RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.INGAME)) {
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Host cannot be updated while a game is in progress.", p);
                                return;
                            }
                            SignGUI getPlayer = new SignGUI(RebornCore.getRebornCore());
                            ArrayList<String> def = new ArrayList<>();
                            def.add(0, "");
                            def.add(1, "^^^^^^^^^");
                            def.add(2, "Game Host");
                            def.add(3, "<SET GME HST>");
                            getPlayer.open(p, def.toArray(new String[def.size()]), new SignGUI.SignGUIListener() {
                                @Override
                                public void onSignDone(Player player, String[] lines) {
                                    String s = ChatColor.stripColor(lines[0].replaceAll("\"", "").replaceAll("\'", ""));
                                    if (s == null || s.length() == 0) {
                                        UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Please enter a player name.", player);
                                        return;
                                    }
                                    try {
                                        String old = currentHost;
                                        Score c = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW + old);
                                        int val = c.getScore();
                                        Bukkit.getScoreboardManager().getMainScoreboard().resetScores(ChatColor.YELLOW + old);
                                        Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW + s).setScore(val);
                                    } catch (Exception e) {
                                        //Catches exception if scoreboard isnt registered ie in lobby and nothing needs to be done about this
                                    }
                                    currentHost = s;
                                    Bukkit.getLogger().info("Set host as \"" + s + "\"");
                                    UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + s + ChatColor.YELLOW + " is now hosting this game.");
                                    ignore.add(p);
                                    openMenu(p);

                                }
                            });
                        } else if (s.contains("Set Slot Count")) {
                            if (RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.INGAME)) {
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Slot count cannot be updated while a game is in progress.", p);
                                return;
                            }
                            SignGUI getPlayer = new SignGUI(RebornCore.getRebornCore());
                            ArrayList<String> def = new ArrayList<>();
                            def.add(0, "");
                            def.add(1, "^^^^^^^^^");
                            def.add(2, "Slot Count");
                            def.add(3, "<SET SLT CNT>");
                            getPlayer.open(p, def.toArray(new String[def.size()]), new SignGUI.SignGUIListener() {
                                @Override
                                public void onSignDone(Player player, String[] lines) {
                                    String s = ChatColor.stripColor(lines[0].replaceAll("\"", "").replaceAll("\'", ""));
                                    Integer slots = 80;
                                    try {
                                        slots = Integer.parseInt(s);
                                    } catch (Exception e) {
                                        UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Please enter a valid slot count.", p);
                                        return;
                                    }
                                    try {
                                        int old = GameManager.slots;
                                        Score c = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW + String.valueOf(old));
                                        int val = c.getScore();
                                        Bukkit.getScoreboardManager().getMainScoreboard().resetScores(ChatColor.YELLOW + String.valueOf(old));
                                        Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW + String.valueOf(slots)).setScore(val);
                                    } catch (Exception e) {
                                        //Catches exception if scoreboard isnt registered ie in lobby
                                    }
                                    GameManager.slots = slots;
                                    RebornCore.getCoveAPI().getGame().setMaxPlayers(slots);
                                    UltraHardcoreReddit.broadcastUHCMessage(ChatColor.YELLOW + "Slot count has been set to " + ChatColor.GREEN + s);
                                    ignore.add(p);
                                    openMenu(p);
                                }
                            });
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
                if (event.getView().getTopInventory().getTitle().contains("Game Manager")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> GameMenu.openMenu(p), 5L);

                }
            }
        }
    }

}
