package network.reborn.core.Listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.proxy.API.Notify;
import network.reborn.proxy.RebornProxy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

    @EventHandler
    public void onIntentoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("My Profile")) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            if (item.getItemMeta().hasDisplayName() && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("My Settings")) {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(rebornPlayer.getSettingsGUI());
            }
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("My Settings")) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            if (item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                boolean enable;
                switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                    default:
                        return;
                    case "Back":
                        event.getWhoClicked().openInventory(rebornPlayer.getMyProfileGUI());
                        return;
                    case "Staff":
                        event.getWhoClicked().openInventory(rebornPlayer.getStaffSettingsGUI());
                        return;
                    case "Enabled":
                        enable = false;
                        break;
                    case "Disabled":
                        enable = true;
                        break;
                }
                // Find 9 items before as that's the one we need to "toggle"
                int slot = event.getSlot() - 9;
                ItemStack toggleItem = event.getInventory().getItem(slot);
                switch (ChatColor.stripColor(toggleItem.getItemMeta().getDisplayName())) {
                    default:
                        break;
                    case "Chat Notifications":
                        rebornPlayer.setChatAlerts(enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                    case "Double Jump":
                        rebornPlayer.setDoubleJump(enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                    case "Chat Enabled":
                        rebornPlayer.setChatEnabled(enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                }
                event.getWhoClicked().openInventory(rebornPlayer.getSettingsGUI());
            }
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("Staff Settings")) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            if (item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                boolean enable;
                switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                    default:
                        return;
                    case "Back":
                        event.getWhoClicked().openInventory(rebornPlayer.getSettingsGUI());
                        return;
                    case "Enabled":
                        enable = false;
                        break;
                    case "Disabled":
                        enable = true;
                        break;
                }
                // Find 9 items before as that's the one we need to "toggle"
                int slot = event.getSlot() - 9;
                ItemStack toggleItem = event.getInventory().getItem(slot);
                switch (ChatColor.stripColor(toggleItem.getItemMeta().getDisplayName())) {
                    default:
                        break;
                    case "Report Alerts":
                        rebornPlayer.setNotify(Notify.REPORT, enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                    case "Cheat Alerts":
                        rebornPlayer.setNotify(Notify.CHEAT, enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                    case "Staff Chat":
                        rebornPlayer.setNotify(Notify.STAFF_CHAT, enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                    case "Social Spy":
                        rebornPlayer.setNotify(Notify.SOCIAL_SPY, enable);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 1, 1);
                        break;
                }
                event.getWhoClicked().openInventory(rebornPlayer.getSettingsGUI());
            }
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("Servers")) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            Player player = (Player) event.getWhoClicked();
            if (item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                String name = item.getItemMeta().getDisplayName();
                name = ChatColor.stripColor(name);
                String server = name.replaceAll("[\\D]+", "");
                ServerInfo serverInfo = RebornProxy.getProxyInstance().getProxy().getServerInfo(server);
                if (serverInfo == null) {
                    player.sendMessage(ChatColor.RED + "Server not found");
                    return;
                }

                if (player instanceof ProxiedPlayer) {
                    player.sendMessage(ChatColor.GREEN + "Sending you to " + serverInfo.getName());
                    ProxiedPlayer proxiedPlayer = (ProxiedPlayer) player;
                    proxiedPlayer.connect(serverInfo);
                } else {
                    player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You can only run this command as a player");
                }

            }
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().startsWith(ChatColor.stripColor("Rank"))) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            String player = event.getInventory().getTitle().split(" ")[2];
            if (item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                switch (ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase())) {
                    default:
                        return;
                    case "default":
                        if (item.getItemMeta().getLore().contains("donor")) {
                            if (RebornCore.getCoveAPI().setDefaultRank(player, false)) {
                                rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Updated rank");
                            } else {
                                rebornPlayer.getPlayer().sendMessage(ChatColor.RED + "Rank update failed");
                            }
                        } else if (item.getItemMeta().getLore().contains("server")) {
                            if (RebornCore.getCoveAPI().setDefaultRank(player, true)) {
                                rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Updated rank");
                            } else {
                                rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Rank update failed");
                            }
                        } else {
                            rebornPlayer.getPlayer().sendMessage(ChatColor.RED + "Rank update failed");
                        }
                        break;
                    case "vip+":
                        if (RebornCore.getCoveAPI().updatePlayerRank(player, "VIPPLUS")) {
                            rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Updated rank");
                        } else {
                            rebornPlayer.getPlayer().sendMessage(ChatColor.RED + "Rank update failed");
                        }
                        break;
                    case "media":
                    case "helper":
                    case "moderator":
                    case "senior":
                    case "admin":
                    case "vip":
                    case "reborn":
                        if (RebornCore.getCoveAPI().updatePlayerRank(player, ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase()))) {
                            rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Updated rank");
                        } else {
                            rebornPlayer.getPlayer().sendMessage(ChatColor.RED + "Rank update failed");
                        }
                        break;
                }
                rebornPlayer.getPlayer().closeInventory();
            }
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().startsWith(ChatColor.stripColor("Report"))) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            String player = event.getInventory().getTitle().split(" ")[2];
            if (item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                ServerRank rank = rebornPlayer.getServerRank();
                Player sender = rebornPlayer.getPlayer();
                RebornProxy.getRebornAPI().notifyStaff("&7[&cReport&7] " + rank.getNiceName(true).toUpperCase() +
                        ChatColor.WHITE + sender.getName() + ChatColor.GRAY + ": " +
                        ChatColor.YELLOW + player + ChatColor.GRAY + ": " +
                        ChatColor.RESET + item.getItemMeta().getDisplayName(), Notify.REPORT);
            }
            rebornPlayer.getPlayer().closeInventory();
        } else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().startsWith(ChatColor.stripColor("Server Manager"))) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            ItemStack item = event.getCurrentItem();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) event.getWhoClicked());
            if (item.getType() == Material.BARRIER) {
                event.setCancelled(true);
                String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().split(" ")[0]);
                switch (name) {
                    default:
                        return;
                    case "RESTART":
                        RebornCore.getCoveAPI().getOnlineCovePlayers().forEach(RebornPlayer::sendToRandomHub);
                        Bukkit.shutdown();
                        break;
                    case "WHITELIST":
                        if (Bukkit.hasWhitelist()) {
                            Bukkit.setWhitelist(false);
                            rebornPlayer.sendTitle(ChatColor.GREEN + "Whitelist has been disabled", "Anyone may join.", 1 * 20, 5 * 20, 1 * 20);
                        } else {
                            Bukkit.setWhitelist(true);
                            rebornPlayer.sendTitle(ChatColor.RED + "Whitelist has been enabled", "Only Admins+ may join.", 1 * 20, 5 * 20, 1 * 20);
                        }
                        break;
                }
                rebornPlayer.getPlayer().closeInventory();
            }
        }
    }
}
