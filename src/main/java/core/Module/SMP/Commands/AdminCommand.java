package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AdminCommand implements CommandExecutor {
    public static ArrayList<UUID> admins = new ArrayList<>();
    public static HashMap<UUID, ItemStack[]> inventories = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }

            if (admins.contains(((Player) sender).getUniqueId())) {
                admins.remove(((Player) sender).getUniqueId());
                if (inventories.containsKey(((Player) sender).getUniqueId())) {
                    ((Player) sender).getInventory().clear();
                    ((Player) sender).getInventory().setContents(inventories.get(((Player) sender).getUniqueId()));
                    inventories.remove(((Player) sender).getUniqueId());
                }
                ((Player) sender).setAllowFlight(false);
                sender.sendMessage(ChatColor.GREEN + "You are no longer in admin mode");
            } else {
                admins.add(((Player) sender).getUniqueId());
                inventories.put(((Player) sender).getUniqueId(), ((Player) sender).getInventory().getContents());
                ((Player) sender).getInventory().clear();
                ((Player) sender).setAllowFlight(true);
                sender.sendMessage(ChatColor.GREEN + "You are now in admin mode");
            }
        }

        return true;
    }
}