package network.reborn.core.Module.Hub.Listeners;

import network.reborn.core.API.Module;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {
    private Hub hub;

    public PlayerInteract(Hub hub) {
        this.hub = hub;
//		Bukkit.getScheduler().runTaskAsynchronously(RebornCore.getRebornCore(), () -> {
//			Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(), () -> {
//			RebornCore.getRebornAPI().getOnlineCovePlayers().stream().filter(covePlayer -> Hub.parkourPlayers.containsKey(covePlayer.getUUID())).forEach(covePlayer -> covePlayer.sendActionBar(ChatColor.GOLD + "Time: " + OtherUtil.getDurationString((System.currentTimeMillis() - Hub.parkourPlayers.get(covePlayer.getUUID())) / 1000, false)));
//			}, 20, 20);
//		});
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
//		if (Hub.parkourPlayers.containsKey(event.getPlayer().getUniqueId()))
//			Hub.parkourPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().contains("CHEST"))
            event.setCancelled(true);

//		if (event.getAction().equals(Action.PHYSICAL)) {
//			if (event.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
//				if (Hub.parkourPlayers.containsKey(event.getPlayer().getUniqueId())) {
//					Hub.parkourEnd.forEach(location -> {
//						if (location.getBlockX() == event.getClickedBlock().getX() && location.getBlockZ() == event.getClickedBlock().getZ()) {
//							Hub.parkourPlayers.remove(event.getPlayer().getUniqueId());
//							event.getPlayer().sendMessage(ChatColor.GREEN + "You finished the parkour!");
//						} else {
//							System.out.println("No Ending");
//						}
//					});
//				} else {
//					System.out.println("SUCCESS 4");
//					Hub.parkourStart.forEach(location -> {
//						if (location.getBlockX() == event.getClickedBlock().getX() && location.getBlockZ() == event.getClickedBlock().getZ()) {
//							Hub.parkourPlayers.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
//							event.getPlayer().sendMessage(ChatColor.GREEN + "You started the parkour!");
//							Bukkit.getScheduler().runTaskAsynchronously(RebornCore.getRebornCore(), () -> {
//								Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(), () -> {
//									RebornCore.getRebornAPI().getCovePlayer(event.getPlayer().getUniqueId()).sendActionBar(ChatColor.GOLD + "Time: " + OtherUtil.getDurationString((System.currentTimeMillis() - Hub.parkourPlayers.get(event.getPlayer().getUniqueId())) / 1000, false));
//								}, 20, 20);
//							});
//						} else {
//							System.out.println("No Starting");
//						}
//					});
//				}
//			}
//			return;
//		}

        if (event.getItem() == null || event.getItem().getType() == Material.AIR || !event.getItem().hasItemMeta())
            return;
        ItemStack item = event.getItem();
        if (event.getAction().toString().contains("LEFT"))
            return;// Don't do left clicks
        if (item.getType().equals(Material.INK_SACK) && item.getData().getData() == 8) { // Show Players was clicked
            hub.showPlayers(event.getPlayer());
        } else if (item.getType().equals(Material.INK_SACK) && item.getData().getData() == 10) { // Hide Players was clicked
            hub.hidePlayers(event.getPlayer());
        } else if (item.getType().equals(Material.COMPASS)) {
            event.getPlayer().openInventory(Hub.serverSelector.getServerSelector());
        } else if (item.getType().equals(Material.ENDER_CHEST)) {
            event.getPlayer().openInventory(Hub.cosmetics.getCoveBox());
        } else if (item.getType().equals(Material.SKULL_ITEM)) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
            if (rebornPlayer.canPlayer(ServerRank.ADMIN))
                event.getPlayer().openInventory(rebornPlayer.getMyProfileGUI());
            else
                event.getPlayer().sendMessage(ChatColor.RED + "Coming Soon");
        } else if (item.getType().equals(Material.WATCH)) {
            event.getPlayer().performCommand("sm");
        } else if (item.getType().equals(Material.EMERALD)) {

            String name = event.getItem().getItemMeta().getDisplayName();

            switch (ChatColor.stripColor(name.toLowerCase()).replaceAll("shop \\(right click\\)", "").trim()) {
                case "skywars":
                    Hub.skyWarsShop.openGUI(event.getPlayer());
                    break;
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NPC")) {
            if (event.getRightClicked().getName().equalsIgnoreCase(String.valueOf(ChatColor.LIGHT_PURPLE))) {
                // SkyWars
                Hub.lobbies.put(event.getPlayer().getUniqueId(), Module.SKYWARS);
                Hub.syncPlayerBasedOnLobby(event.getPlayer());
            } else if (event.getRightClicked().getName().equalsIgnoreCase(String.valueOf(ChatColor.AQUA))) {
                //UHC
                Hub.lobbies.put(event.getPlayer().getUniqueId(), Module.ULTRA_HARDCORE);
                Hub.syncPlayerBasedOnLobby(event.getPlayer());
            } else if (event.getRightClicked().getName().equals(String.valueOf(ChatColor.DARK_RED))) {
                Hub.lobbies.put(event.getPlayer().getUniqueId(), Module.UHC_REDDIT);
                Hub.syncPlayerBasedOnLobby(event.getPlayer());
            }
        }
    }

}
