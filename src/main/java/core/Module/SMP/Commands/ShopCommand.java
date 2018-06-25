package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.Module.SMP.SMP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            SMP.shop.openShopGUI((Player) sender);
//            ((Player) sender).openInventory(Factions.shop.foodItemPages.get(0));
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be ran by a player");
        }
        return true;
    }

}
