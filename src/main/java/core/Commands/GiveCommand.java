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
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length > 3 || args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /give (item) (ammount) [player]");
            return true;
        }

        try {
            int i = Integer.parseInt(args[1].replaceAll("\\D+", ""));
            Material m = Material.getMaterial(args[0].toUpperCase());
            ItemStack is = new ItemStack(m, i);

            if (args.length == 2) {
                Player p = (Player) sender;
                p.getInventory().addItem(is);
                p.sendMessage(ChatColor.GRAY + "You have been given "
                        + ChatColor.YELLOW + i + ChatColor.GRAY + " of "
                        + ChatColor.YELLOW + m.name());
            } else {
                if (args[2].equalsIgnoreCase("all")) {
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        p.getInventory().addItem(is);
                        p.sendMessage(ChatColor.GRAY + "Everyone has been given "
                                + ChatColor.YELLOW + i + ChatColor.GRAY + " of "
                                + ChatColor.YELLOW + m.name() + ChatColor.GRAY + " by "
                                + ChatColor.YELLOW + sender.getName());
                    }
                } else {
                    Player p = Bukkit.getPlayer(args[2]);
                    if (p != null) {
                        p.getInventory().addItem(is);
                        p.sendMessage(ChatColor.GRAY + "You have been given "
                                + ChatColor.YELLOW + i + ChatColor.GRAY + " of "
                                + ChatColor.YELLOW + m.name() + ChatColor.GRAY + " by "
                                + ChatColor.YELLOW + sender.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                    }
                }
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Material not found! (Try using F3 + H and the creative inventory)");
            return true;
        }

        return true;
    }
}