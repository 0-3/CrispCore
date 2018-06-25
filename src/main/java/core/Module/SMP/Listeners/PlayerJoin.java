package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Events.PlayerRunJoinEvent;
import network.reborn.core.Module.SMP.Handlers.HomesHandler;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerRunJoin(PlayerRunJoinEvent event) {
        RebornPlayer rebornPlayer = event.getPlayer();
        Bukkit.broadcastMessage(ChatColor.GOLD + rebornPlayer.getName() + " has joined the server");
        if (!rebornPlayer.hasPermission("smp.played")) {
            rebornPlayer.givePermission("smp.played");
            rebornPlayer.setBalance("SMP", 500, true);
            rebornPlayer.getPlayer().teleport(SMP.spawn);
            rebornPlayer.getPlayer().closeInventory();
            rebornPlayer.getPlayer().getInventory().clear();
            rebornPlayer.getPlayer().getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            rebornPlayer.getPlayer().getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            rebornPlayer.getPlayer().getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            rebornPlayer.getPlayer().getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SPADE));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_SPADE));
            rebornPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));

            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + rebornPlayer.getName() + ChatColor.GREEN + " has joined for the first time!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        // Load the homes of the player
        Bukkit.getScheduler().runTaskAsynchronously(RebornCore.getRebornCore(), () -> {
            String query = "SELECT * FROM `smp_homes` WHERE `UUID` = '" + event.getPlayer().getUniqueId().toString() + "';";
            try {
                ResultSet resultSet = RebornCore.getCoveAPI().getMySQLManager().getConnection().createStatement().executeQuery(query);
                if (resultSet.next()) {
                    resultSet.beforeFirst();
                    HashMap<String, Location> homes = new HashMap<>();
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String locationStr = resultSet.getString("location");
                        String[] locationArray = locationStr.split("\\|");
                        Location location = new Location(Bukkit.getWorld(locationArray[0]), Double.parseDouble(locationArray[1]), Double.parseDouble(locationArray[2]), Double.parseDouble(locationArray[3]), Float.parseFloat(locationArray[4]), Float.parseFloat(locationArray[5]));
                        homes.put(name, location);
                    }
                    HomesHandler.homes.put(event.getPlayer().getUniqueId(), homes);
                }
            } catch (SQLException ignored) {
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        event.setQuitMessage(ChatColor.GOLD + rebornPlayer.getName() + " has left the server");
    }

}
