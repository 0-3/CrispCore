package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Lag;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        String memory = ChatColor.GREEN + Lag.getUsedRAM() + "/" + Lag.getMaxRam() + " MB";
        String tps = ChatColor.GREEN + "" + Math.round(Lag.getTPS() * 100.0) / 100.0 + "/20 TPS";

        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer((Player) sender);
            rebornPlayer.sendCentredMessage(" ");
            rebornPlayer.sendCentredMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "-=-=-=-=- Reborn Network Lag -=-=-=-=-");
            rebornPlayer.sendCentredMessage(" ");
            rebornPlayer.sendCentredMessage(ChatColor.AQUA + "Memory: " + memory);
            rebornPlayer.sendCentredMessage(ChatColor.AQUA + "TPS: " + tps);
            rebornPlayer.sendCentredMessage(" ");
        } else {
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "-=-=-=-=- Reborn Network Lag -=-=-=-=-");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "Memory: " + memory);
            sender.sendMessage(ChatColor.AQUA + "TPS: " + tps);
            sender.sendMessage(" ");
        }
        return true;
    }

}
