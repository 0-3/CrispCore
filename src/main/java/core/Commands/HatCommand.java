package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command must be ran as a player");
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0"))) {
            PlayerInventory inv = player.getInventory();
            ItemStack helmet = inv.getHelmet();
            if (helmet == null || helmet.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You currently don't have a hat");
            } else {
                ItemStack air = new ItemStack(Material.AIR);
                inv.setHelmet(air);
                player.sendMessage(ChatColor.GREEN + "Hat removed");
            }
        } else {
            if (player.getItemInHand().getType() != Material.AIR) {
                ItemStack hand = player.getItemInHand();
                if (hand.getType().getMaxDurability() == 0) {
                    PlayerInventory inv = player.getInventory();
                    ItemStack helmet = inv.getHelmet();
                    inv.setHelmet(hand);
                    inv.setItemInHand(helmet);
                    player.sendMessage(ChatColor.GREEN + "Hat swapped");
                } else {
                    player.sendMessage(ChatColor.RED + "You currently have armor on");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have an item in your hand");
            }
        }
        return true;
    }

}
