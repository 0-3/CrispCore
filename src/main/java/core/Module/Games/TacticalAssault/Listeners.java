package network.reborn.core.Module.Games.TacticalAssault;

import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.Games.GamePlayer;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.RebornCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TacticalAssault.selectedMap.sendPlayerToSpawn(event.getPlayer());
        TacticalAssault.teams.put(TacticalAssault.selectedMap.getSpawn(event.getPlayer()), event.getPlayer().getName());
        TacticalAssault.players.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TacticalAssault.selectedMap.removePlayerFromSpawns(event.getPlayer());
        TacticalAssault.players.remove(event.getPlayer().getUniqueId());
        for (Map.Entry<Integer, String> entry : TacticalAssault.teams.entrySet()) {
            System.out.println("SPAWNID: " + entry.getKey() + " | PLAYER:  " + entry.getValue());
            if (entry.getValue().toLowerCase().equals(event.getPlayer().getName().toLowerCase())) {
                TacticalAssault.teams.remove(entry.getKey());
            }
        }
        if (RebornCore.getCoveAPI().getGame().getGameState() == GameState.INGAME) {
            TacticalAssault.checkLastPlayer();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDamage(PlayerDamageEvent event) {
        GamePlayer gamePlayer = RebornCore.getCoveAPI().getGamePlayer(event.getPlayer());
        if (gamePlayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        for (Block b : getNearbyBlocks(event.getBlockPlaced().getLocation(), 2)) {
            if (b.getType().equals(Material.WORKBENCH) || b.getType().equals(Material.DISPENSER)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot place that block here.");
                event.setCancelled(true);
                continue;
            }
        }
        if (event.getBlock().getType().equals(Material.TNT) && !event.isCancelled()) {
            event.getBlock().setType(Material.AIR);
            TacticalAssault.selectedMap.getWorld().spawnEntity(new Location(TacticalAssault.selectedMap.getWorld(), event.getBlock().getLocation().getBlockX() + .5, event.getBlock().getLocation().getBlockY() + .5, event.getBlock().getLocation().getBlockZ() + .5), EntityType.PRIMED_TNT);
        } else {
            if (!event.isCancelled()) {
                TacticalAssault.blocksPlaced.add(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getName().toLowerCase().startsWith("generator")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Location loc = TacticalAssault.openInventory.get(player.getUniqueId());
            if (event.getCurrentItem().getType().equals(Material.IRON_INGOT)) {
                if (TacticalAssault.dispenserIronLevels.get(loc) < 1 && !TacticalAssault.dispensers.contains(loc)) {
                    // Mid-Dispensers have a fixed item.
                    player.sendMessage(ChatColor.RED + "Generator Broken..");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                } else {
                    TacticalAssault.dispenserSelections.put(loc, 0);
                    player.closeInventory();
                }
            } else if (event.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
                if (TacticalAssault.dispenserGoldLevels.get(loc) < 1 && !TacticalAssault.dispensers.contains(loc)) {
                    // Mid-Dispensers have a fixed item.
                    player.sendMessage(ChatColor.RED + "Generator Broken..");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                } else {
                    TacticalAssault.dispenserSelections.put(loc, 1);
                    player.closeInventory();
                }
            } else if (event.getCurrentItem().getType().equals(Material.DIAMOND)) {
                if (TacticalAssault.dispenserDiamLevels.get(loc) < 1 && !TacticalAssault.dispensers.contains(loc)) {
                    // Mid-Dispensers have a fixed item.
                    player.sendMessage(ChatColor.RED + "Generator Broken..");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                } else {
                    TacticalAssault.dispenserSelections.put(loc, 2);
                    player.closeInventory();
                }
            } else if (event.getCurrentItem().getType().equals(Material.ANVIL)) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("FULLY UPGRADED")) {
                    player.sendMessage(ChatColor.GREEN + "Already Upgraded");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                } else {
                    if (TacticalAssault.dispenserSelections.get(loc) == 0) {
                        int level = TacticalAssault.dispenserIronLevels.get(loc);
                        if (level == 1) {
                            ItemStack price = new ItemStack(Material.IRON_INGOT);
                            int priceNum = 20;
                            if (player.getInventory().containsAtLeast(price, priceNum)) {
                                int i = 0;
                                while (i < priceNum) {
                                    player.getInventory().removeItem(price);
                                    i++;
                                }
                                TacticalAssault.dispenserIronLevels.put(loc, TacticalAssault.dispenserIronLevels.get(loc) + 1);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't afford that! PRICE: 20 Iron Ingots");
                                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                            }
                        } else if (level == 2) {
                            ItemStack price = new ItemStack(Material.GOLD_INGOT);
                            int priceNum = 10;
                            if (player.getInventory().containsAtLeast(price, priceNum)) {
                                int i = 0;
                                while (i < priceNum) {
                                    player.getInventory().removeItem(price);
                                    i++;
                                }
                                TacticalAssault.dispenserIronLevels.put(loc, TacticalAssault.dispenserIronLevels.get(loc) + 1);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't afford that! PRICE: 10 Gold Ingots");
                                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                            }
                        } else if (level == 3) {
                            ItemStack price = new ItemStack(Material.GOLD_INGOT);
                            int priceNum = 50;
                            if (player.getInventory().containsAtLeast(price, priceNum)) {
                                int i = 0;
                                while (i < priceNum) {
                                    player.getInventory().removeItem(price);
                                    i++;
                                }
                                TacticalAssault.dispenserIronLevels.put(loc, TacticalAssault.dispenserIronLevels.get(loc) + 1);
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't afford that! PRICE: 50 Gold Ingots");
                                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, (float) 0.5, 1);
                            }
                        }
                    }
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, (float) 0.5, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (block.getType().equals(Material.WORKBENCH)) {
                event.setCancelled(true);
                TacticalAssault.shop.openShopGUI(event.getPlayer());
            } else if (block.getType().equals(Material.DISPENSER)) {
                event.setCancelled(true);
                Inventory inventory = Bukkit.createInventory(null, 27, "Generator");
                ItemStack iron = new ItemStack(Material.IRON_INGOT);
                ItemMeta ironMeta = iron.getItemMeta();
                ironMeta.setDisplayName(ChatColor.GOLD + "Iron - Level " + TacticalAssault.dispenserIronLevels.get(block.getLocation()));
                iron.setItemMeta(ironMeta);
                if (TacticalAssault.dispenserSelections.get(block.getLocation()) == 0) {
                    ironMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    iron.setItemMeta(ironMeta);
                    iron.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
                }
                inventory.setItem(0, iron);
                ItemStack gold = new ItemStack(Material.GOLD_INGOT);
                ItemMeta goldMeta = gold.getItemMeta();
                goldMeta.setDisplayName(ChatColor.GOLD + "Gold - Level " + TacticalAssault.dispenserGoldLevels.get(block.getLocation()));
                gold.setItemMeta(goldMeta);
                if (TacticalAssault.dispenserSelections.get(block.getLocation()) == 1) {
                    goldMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    gold.setItemMeta(goldMeta);
                    gold.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
                }
                inventory.setItem(1, gold);
                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemMeta diamondMeta = diamond.getItemMeta();
                diamondMeta.setDisplayName(ChatColor.GOLD + "Diamond - Level " + TacticalAssault.dispenserDiamLevels.get(block.getLocation()));
                diamond.setItemMeta(diamondMeta);
                if (TacticalAssault.dispenserSelections.get(block.getLocation()) == 2) {
                    diamondMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    diamond.setItemMeta(diamondMeta);
                    diamond.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
                }
                inventory.setItem(2, diamond);
                ItemStack upgradeItem = new ItemStack(Material.ANVIL);
                ItemMeta upgradeMeta = upgradeItem.getItemMeta();
                if (TacticalAssault.getLevelFromSelectedItem(TacticalAssault.dispenserSelections.get(block.getLocation()), block.getLocation()) == 4) {
                    upgradeMeta.setDisplayName(ChatColor.GREEN + "FULLY UPGRADED");
                } else {
                    upgradeMeta.setDisplayName(ChatColor.DARK_GREEN + "Upgrade Generator");
                }
                upgradeItem.setItemMeta(upgradeMeta);
                inventory.setItem(13, upgradeItem);
                event.getPlayer().openInventory(inventory);
                TacticalAssault.openInventory.put(event.getPlayer().getUniqueId(), block.getLocation());
            }
        } else {
            if (block.getType().equals(Material.BANNER) || block.getType().equals(Material.STANDING_BANNER)) {
                int playerTeam = TacticalAssault.getTeam(event.getPlayer());
                if (block.equals(TacticalAssault.bannerLocations.get(playerTeam).getBlock())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot destroy your own banner!");
                } else {
                    for (Map.Entry<Integer, Location> entry : TacticalAssault.bannerLocations.entrySet()) {
                        int bannerx = entry.getValue().getBlockX();
                        int bannery = entry.getValue().getBlockY();
                        int bannerz = entry.getValue().getBlockZ();
                        int blockx = block.getLocation().getBlockX();
                        int blocky = block.getLocation().getBlockY();
                        int blockz = block.getLocation().getBlockZ();
                        if (bannerx == blockx && bannery == blocky && bannerz == blockz) {
                            TacticalAssault.bannerDestroyed.put(entry.getKey(), true);
                            event.setCancelled(true);
                            block.setType(Material.AIR);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, (float) 0.5, 1);
                            }
                            if (TacticalAssault.teams.get(entry.getKey()) == null) {
                                return;
                            }
                            Bukkit.broadcastMessage(ChatColor.GREEN + TacticalAssault.teams.get(entry.getKey()) + "'s banner has been destroyed by " + event.getPlayer().getName() + "!");
                            RebornCore.getCoveAPI().getGamePlayer(event.getPlayer()).giveBalance(RebornCore.getCoveAPI().getGame().getSlug(), RebornCore.getCoveAPI().getGame().getCoinsPerKill(), true, true, ChatColor.GOLD + "+%amount% Coins");
                            continue;
                        }
                    }
                }
            }
        }

    }

    private List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().toUpperCase().contains("BANNER")) {
            int playerTeam = TacticalAssault.getTeam(event.getPlayer());
            if (event.getBlock().getLocation().getBlock().equals(TacticalAssault.bannerLocations.get(playerTeam).getBlock())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot destroy your own banner!");
            } else {
                for (Map.Entry<Integer, Location> entry : TacticalAssault.bannerLocations.entrySet()) {
                    int bannerx = entry.getValue().getBlockX();
                    int bannery = entry.getValue().getBlockY();
                    int bannerz = entry.getValue().getBlockZ();
                    int blockx = event.getBlock().getLocation().getBlockX();
                    int blocky = event.getBlock().getLocation().getBlockY();
                    int blockz = event.getBlock().getLocation().getBlockZ();
                    if (bannerx == blockx && bannery == blocky && bannerz == blockz) {
                        TacticalAssault.bannerDestroyed.put(entry.getKey(), true);
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, (float) 0.5, 1);
                        }
                        if (TacticalAssault.teams.get(entry.getKey()) == null) {
                            return;
                        }
                        Bukkit.broadcastMessage(ChatColor.GREEN + TacticalAssault.teams.get(entry.getKey()) + "'s banner has been destroyed by " + event.getPlayer().getName() + "!");
                        RebornCore.getCoveAPI().getGamePlayer(event.getPlayer()).giveBalance(RebornCore.getCoveAPI().getGame().getSlug(), RebornCore.getCoveAPI().getGame().getCoinsPerKill(), true, true, ChatColor.GOLD + "+%amount% Coins");
                        continue;
                    }
                }
            }
        } else {
            if (event.getBlock().getType().equals(Material.WORKBENCH)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break that block.");
                event.setCancelled(true);
            } else if (event.getBlock().getType().equals(Material.DISPENSER)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break that block.");
                event.setCancelled(true);
            }
            for (Block b : getNearbyBlocks(event.getBlock().getLocation(), 2)) {
                if (b.getType().equals(Material.WORKBENCH) || b.getType().equals(Material.DISPENSER)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot break that block here.");
                    event.setCancelled(true);
                    continue;
                }
            }
            if (!event.isCancelled()) {
                if (!TacticalAssault.blocksPlaced.contains(event.getBlock().getLocation())) {
                    Block block = event.getBlock();
                    if (!block.getType().equals(Material.GLASS)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed during this game.");
                    }
                } else {
                    TacticalAssault.blocksPlaced.remove(event.getBlock().getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (!TacticalAssault.blocksPlaced.contains(block.getLocation()) && !block.getType().equals(Material.GLASS)) {
                it.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (RebornCore.getCoveAPI().getGame().getGameState() != GameState.INGAME) {
            event.setDeathMessage(null);
        } else {
            Player player = event.getEntity();
            if (event.getEntity().getKiller() != null) {
                if (event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause().getCause() != null && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    event.setDeathMessage(ChatColor.RED + event.getEntity().getDisplayName() + " ↢ " + event.getEntity().getKiller().getDisplayName());
                } else {
                    event.setDeathMessage(ChatColor.RED + event.getEntity().getDisplayName() + " was killed by " + event.getEntity().getKiller().getDisplayName());
                }
            } else {
                event.setDeathMessage(ChatColor.RED + event.getEntity().getDisplayName() + ChatColor.RED + " died");
            }
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setContents(new ItemStack[]{});
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.setSaturation(20);
            player.setFallDistance(0F);
            if (TacticalAssault.bannerDestroyed.get(TacticalAssault.getTeam(player)) == Boolean.valueOf(false)) {
                player.teleport(TacticalAssault.respawnLocations.get(TacticalAssault.getTeam(player)));
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + player.getDisplayName() + ChatColor.RED + " was eliminated from the game.");
                //TODO: Get spectator spawn and teleport to it.
                player.setVelocity(new Vector(0, 1, 0));
                player.setGameMode(GameMode.SPECTATOR);
                TacticalAssault.players.put(player.getUniqueId(), false);
                TacticalAssault.checkLastPlayer();
            }
        }
    }

}
