package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

    String[] name1 = {"Mc", "UHC", "Mineman", "Super", "Insqne", "Crazy", "Funky", "_", "Rebqrn", "Reborn"};
    String[] name2 = {"PvPer", "Gqd", "Legend", "Donqt", "Regen"};
    String[] name3 = {"", "_", "", "", String.valueOf(OtherUtil.randInt(0, 1000)), String.valueOf(OtherUtil.randInt(0, 1000)), String.valueOf(OtherUtil.randInt(0, 1000)), String.valueOf(OtherUtil.randInt(0, 1000)), String.valueOf(OtherUtil.randInt(0, 1000))};

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        String randomName = name1[OtherUtil.randInt(0, name1.length - 1)] + name2[OtherUtil.randInt(0, name2.length - 1)] + name3[OtherUtil.randInt(0, name3.length - 1)];
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                if (args.length > 1) {
                    sender.sendMessage(ChatColor.RED + "Correct Usage: /nick (name/off)");
                    return true;
                } else if (args.length > 0) {
                    String name = args[0];
                    if (name.equalsIgnoreCase("reset") || name.equalsIgnoreCase("none") || name.equalsIgnoreCase("off") || name.equalsIgnoreCase("disable")) {
                        rebornPlayer.setNick(null);
                        sender.sendMessage(ChatColor.GREEN + "You have been un-nicked");
                    } else {
                        rebornPlayer.setNick(name);
                        sender.sendMessage(ChatColor.GREEN + "You have been nicked");
                    }
                } else {
                    rebornPlayer.setNick(randomName);
                    sender.sendMessage(ChatColor.GREEN + "You have been nicked");
                    return true;
                }
            } else if (rebornPlayer.isPlayer(ServerRank.SENIOR) || rebornPlayer.isPlayer(ServerRank.MEDIA)) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Correct Usage: /nick (off)");
                    return true;
                } else {
                    String name = args[0];
                    if (name.equalsIgnoreCase("reset") || name.equalsIgnoreCase("none") || name.equalsIgnoreCase("off") || name.equalsIgnoreCase("disable")) {
                        rebornPlayer.setNick(null);
                        sender.sendMessage(ChatColor.GREEN + "You have been un-nicked");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Correct Usage: /nick (off)");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }
        return true;
    }

}
