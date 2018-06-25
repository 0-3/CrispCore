package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command must be ran as a player");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /invsee <player>");
            return true;
        }

        Player invSeePlayer = Bukkit.getPlayer(args[0]);
        if (invSeePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        Inventory playerInv = invSeePlayer.getInventory();
        Inventory invent = Bukkit.createInventory(null, 54, invSeePlayer.getName() + "'s Inventory");

        for (int i = 0; i < 36; i++) {
            invent.setItem(i, playerInv.getItem(i));
        }
        for (int i = 36; i < 45; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
            ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.MAGIC + "rfudUDWOU139");
            glass.setItemMeta(glassMeta);
            invent.setItem(i, glass);
            invent.setItem(50, glass);
        }
        invent.setItem(45, invSeePlayer.getItemInHand());
        invent.setItem(46, invSeePlayer.getInventory().getHelmet());
        invent.setItem(47, invSeePlayer.getInventory().getChestplate());
        invent.setItem(48, invSeePlayer.getInventory().getLeggings());
        invent.setItem(49, invSeePlayer.getInventory().getBoots());


        //Health, Food & Coord Items
        ItemStack coords = new ItemStack(Material.COMPASS);
        ItemMeta coordsMeta = coords.getItemMeta();
        double x = invSeePlayer.getLocation().getBlockX();
        double y = invSeePlayer.getLocation().getBlockY();
        double z = invSeePlayer.getLocation().getBlockZ();
        coordsMeta.setDisplayName(ChatColor.GRAY + "Coordinates: "
                + ChatColor.YELLOW + x + ChatColor.GRAY + ", "
                + ChatColor.YELLOW + y + ChatColor.GRAY + ", "
                + ChatColor.YELLOW + z);
        coords.setItemMeta(coordsMeta);
        invent.setItem(51, coords);

        ItemStack health = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta healthMeta = health.getItemMeta();
        healthMeta.setDisplayName(ChatColor.GRAY + "Health Level: "
                + ChatColor.YELLOW + invSeePlayer.getHealth());
        health.setItemMeta(healthMeta);
        invent.setItem(52, health);

        ItemStack food = new ItemStack(Material.COOKED_BEEF);
        ItemMeta foodMeta = food.getItemMeta();
        foodMeta.setDisplayName(ChatColor.GRAY + "Food Level: "
                + ChatColor.YELLOW + invSeePlayer.getFoodLevel());
        food.setItemMeta(foodMeta);
        invent.setItem(53, food);

        player.closeInventory();
        player.openInventory(invent);
        return true;
    }
}