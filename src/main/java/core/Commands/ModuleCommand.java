package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModuleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.isPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "-=- Reborn Network Modules -=-");
            if (RebornCore.getCoveAPI().getModule() != null)
                sender.sendMessage(ChatColor.AQUA + "Active Module: " + RebornCore.getCoveAPI().getModule().getName());
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.AQUA + "-=- Cove Network Module Manager -=-");
                sender.sendMessage(ChatColor.AQUA + "/module help - Show this help menu");
                sender.sendMessage(ChatColor.AQUA + "/module <name> - Switch to a module");
                sender.sendMessage(ChatColor.AQUA + "/module reload - Reload and activate new module");
            } else if (args[0].equalsIgnoreCase("reload")) {
                RebornCore.getCoveAPI().reloadModule();
                sender.sendMessage(ChatColor.GREEN + "Reloaded modules");
            } else if (RebornCore.getCoveAPI().getModule() != null && args[0].equalsIgnoreCase(RebornCore.getCoveAPI().getModule().getSlug())) {
                sender.sendMessage(ChatColor.RED + "Module already active");
            } else {
                RebornCore.getRebornCore().getConfig().set("Module", args[0].replaceAll("-", ""));
                RebornCore.getRebornCore().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "This server's module has been set to " + args[0]);
                sender.sendMessage(ChatColor.YELLOW + "If you have entered an invalid module players will NOT be able to join the server");
            }
        }

        return true;
    }

}
