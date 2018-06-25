package network.reborn.core.Commands;

import network.reborn.core.API.Module;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.SkyWars.SkyWarsKitManager;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "ver":
                case "version":
                    sender.sendMessage(ChatColor.AQUA + "The current server is running RebornNetwork " + RebornCore.getRebornCore().getDescription().getVersion());
                    break;
                case "invis":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.hidePlayer(player);
                            online.showPlayer(player);
                            player.hidePlayer(online);
                            player.showPlayer(online);
                        }
                    }
                    break;
                case "stop":
                    int time = 30;
                    if (args.length > 1) {
                        try {
                            time = Integer.parseInt(args[1]);
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Invalid number");
                            return true;
                        }
                    }
                    sender.sendMessage(ChatColor.AQUA + "Stopping server in " + time + " seconds...");
                    try {
                        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), Bukkit::shutdown, time * 20);
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "!!! " + ChatColor.RED + "This server will restart in " + time + " seconds " + ChatColor.DARK_RED + "!!!");
                        RebornCore.getCoveAPI().getModule().onDisable();
                        RebornCore.getCoveAPI().getModule().getCoveServer().setOffline();
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "An error occurred while trying to run the shutdown preparation");
                    }
                    break;
                case "cleartitle":
                    if (sender instanceof Player)
                        RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId()).clearTitle();
                    break;
                case "spec":
                case "spectate":
                    if (args.length > 1) {
                        Player spec = Bukkit.getPlayer(args[1]);
                        if (spec == null) {
                            sender.sendMessage(ChatColor.RED + "Player not found");
                        } else if (sender instanceof Player) {
                            ((Player) sender).setGameMode(GameMode.SPECTATOR);
                            ((Player) sender).setSpectatorTarget(spec);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Correct usage: /core spec <player>");
                    }
                    break;
                case "world":
                    if (args.length > 1) {
                        ((Player) sender).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "You are in world: " + ((Player) sender).getWorld().getName());
                        sender.sendMessage(ChatColor.YELLOW + Bukkit.getWorlds().toString());
                    }
                    break;
                case "signdebug":
                    sender.sendMessage("Loaded Signs: " + Hub.signs.size());
                    sender.sendMessage("Waiting Skywars Servers: " + RebornCore.getServers(Module.SKYWARS, GameState.WAITING).size());
                    sender.sendMessage("Waiting UHC Servers: " + RebornCore.getServers(Module.SKYWARS, GameState.WAITING).size());
                    sender.sendMessage("Waiting SMP Servers: " + RebornCore.getServers(Module.SKYWARS, GameState.WAITING).size());
                    sender.sendMessage("Waiting Tactical Assault Servers: " + RebornCore.getServers(Module.TACTICALASSAULT, GameState.WAITING).size());
                    sender.sendMessage("Waiting Reddit UHC Servers: " + RebornCore.getServers(Module.UHC_REDDIT, GameState.WAITING).size() + "/" + RebornCore.getServers(Module.UHC_REDDIT));

                    break;
                case "swkitsgui":
                    SkyWarsKitManager skyWarsKitManager = new SkyWarsKitManager();
                    skyWarsKitManager.openKitGUI((Player) sender);
                    break;
                default:
                    sender.sendMessage(ChatColor.AQUA + "-=- Reborn Network Module Manager -=-");
                    sender.sendMessage(ChatColor.AQUA + "/core help - Show this help menu");
                    sender.sendMessage(ChatColor.AQUA + "/core version - Show the current version the server is running");
                    sender.sendMessage(ChatColor.AQUA + "/core invis - Reload all invisible and vanished players");
                    sender.sendMessage(ChatColor.AQUA + "/core cleartitle - Clear the current title");
                    sender.sendMessage(ChatColor.AQUA + "/core signdebug - Debug lobby signs");
                    sender.sendMessage(ChatColor.AQUA + "/core swkitsgui - Open the SkyWars kit GUI");
                    sender.sendMessage(ChatColor.AQUA + "/core stop <time> - Restart the server with a countdown of time (def. 30 seconds)");
                    sender.sendMessage(ChatColor.AQUA + "/core spectate <player> - Spectate a player in your current lobby");
                    sender.sendMessage(ChatColor.AQUA + "/core world <world> - Display information about worlds / switch current world");
                    break;
            }
            return true;
        }
        sender.sendMessage(ChatColor.AQUA + "-=- Reborn Network Module Manager -=-");
        sender.sendMessage(ChatColor.AQUA + "/core help - Show this help menu");
        sender.sendMessage(ChatColor.AQUA + "/core version - Show the current version the server is running");
        sender.sendMessage(ChatColor.AQUA + "/core invis - Reload all invisible and vanished players");
        sender.sendMessage(ChatColor.AQUA + "/core cleartitle - Clear the current title");
        sender.sendMessage(ChatColor.AQUA + "/core signdebug - Debug lobby signs");
        sender.sendMessage(ChatColor.AQUA + "/core swkitsgui - Open the SkyWars kit GUI");
        sender.sendMessage(ChatColor.AQUA + "/core stop <time> - Restart the server with a countdown of time (def. 30 seconds)");
        sender.sendMessage(ChatColor.AQUA + "/core spectate <player> - Spectate a player in your current lobby");
        sender.sendMessage(ChatColor.AQUA + "/core world <world> - Display information about worlds / switch current world");
        return true;
    }

}
