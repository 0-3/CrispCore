package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BalanceCommand implements CommandExecutor {
    DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            double balance = Double.parseDouble(String.valueOf(rebornPlayer.getBalance("SMP")));
            sender.sendMessage(ChatColor.GREEN + "Current Balance: " + ChatColor.GOLD + "$" + formatter.format(balance));
        }
        return true;
    }
}