package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            Player mcPlayer = (Player) sender;
            switch (string) {
                case "tpa":
                    if (args.length < 1) {
                        mcPlayer.sendMessage(ChatColor.RED + "Usage: /tpa <player>");
                        return true;
                    }
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        if (SMP.teleportRequests.containsKey(mcPlayer.getUniqueId())) {
                            mcPlayer.sendMessage(ChatColor.RED + "You already have a pending teleport request.");
                            return true;
                        }
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        SMP.teleportRequests.put(mcPlayer.getUniqueId(), target.getUniqueId());
                        mcPlayer.sendMessage(ChatColor.GOLD + "Your teleport request to " + target.getName() + ChatColor.GOLD + " has been sent.");
                        mcPlayer.sendMessage(ChatColor.GOLD + "They have 1 minute to respond.");
                        target.sendMessage(ChatColor.GOLD + mcPlayer.getName() + ChatColor.GOLD + " has sent you a teleport request.");
                        target.sendMessage(ChatColor.GOLD + "You can accept it by running " + ChatColor.RED + "/tpaccept " + mcPlayer.getName());
                        target.sendMessage(ChatColor.GOLD + "or reject it by running " + ChatColor.RED + " /tpdeny " + mcPlayer.getName());
                        Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> {
                            if (SMP.teleportRequests.containsKey(mcPlayer.getUniqueId())) {
                                if (SMP.teleportRequests.get(mcPlayer.getUniqueId()).equals(target.getUniqueId())) {
                                    SMP.teleportRequests.remove(mcPlayer.getUniqueId());
                                    mcPlayer.sendMessage(ChatColor.RED + "Your teleport request to " + target.getName() + ChatColor.RED + " has timed out.");
                                    target.sendMessage(ChatColor.RED + "Your teleport request from " + mcPlayer.getName() + ChatColor.RED + " has timed out.");
                                }
                            }
                        }, 60L * 20L);
                        return true;
                    }
                    mcPlayer.sendMessage(ChatColor.RED + "That player is not online.");
                    break;
                case "tpaccept":
                    if (args.length < 1) {
                        mcPlayer.sendMessage(ChatColor.RED + "Please specify which request to accept. Here is a list of requests:");
                        int requestCount = 0;
                        for (Map.Entry<UUID, UUID> entry : SMP.teleportRequests.entrySet()) {
                            if (entry.getValue().equals(mcPlayer.getUniqueId())) {
                                mcPlayer.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(entry.getKey()).getName());
                                requestCount++;
                            }
                        }
                        if (requestCount == 0) {
                            mcPlayer.sendMessage(ChatColor.RED + "No one has requested to teleport to you. :(");
                        }
                        return true;
                    }
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (SMP.teleportRequests.containsKey(target.getUniqueId())) {
                            if (SMP.teleportRequests.get(target.getUniqueId()).equals(mcPlayer.getUniqueId())) {
                                target.sendMessage(ChatColor.GOLD + mcPlayer.getName() + ChatColor.GOLD + " has accepted your teleport request.");
                                target.sendMessage(ChatColor.GOLD + "You will teleport in 5 seconds. Please move around to cancel the teleport.");
                                mcPlayer.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GOLD + " will be teleported to you in 5 seconds unless they cancel.");
                                SMP.pendingTeleports.put(target.getUniqueId(), mcPlayer.getUniqueId());
                                Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> {
                                    if (SMP.pendingTeleports.containsKey(target.getUniqueId())) {
                                        if (SMP.pendingTeleports.get(target.getUniqueId()).equals(mcPlayer.getUniqueId())) {
                                            target.sendMessage(ChatColor.GOLD + "Teleporting...");
                                            mcPlayer.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GOLD + " is now teleporting to you...");
                                            target.teleport(Bukkit.getPlayer(mcPlayer.getUniqueId()).getLocation()); // Used to get most recent location instead of one at time. Not sure if this is neccessary.
                                            SMP.pendingTeleports.remove(target.getUniqueId());
                                            SMP.teleportRequests.remove(target.getUniqueId());
                                        }
                                    }
                                }, 5L * 20L);
                                return true;
                            }
                        }
                    }
                    mcPlayer.sendMessage(ChatColor.RED + "That player did not request to teleport to you!");
                    break;
                case "tpdeny":
                    if (args.length < 1) {
                        mcPlayer.sendMessage(ChatColor.RED + "Please specify which request to deny. Here is a list of requests:");
                        int requestCount = 0;
                        for (Map.Entry<UUID, UUID> e : SMP.teleportRequests.entrySet()) {
                            if (e.getValue().equals(mcPlayer.getUniqueId())) {
                                mcPlayer.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(e.getKey()).getName());
                                requestCount++;
                            }
                        }
                        if (requestCount == 0) {
                            mcPlayer.sendMessage(ChatColor.RED + "No one has requested to teleport to you. :(");
                        }
                        return true;
                    }
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (SMP.teleportRequests.containsKey(target.getUniqueId())) {
                            if (SMP.teleportRequests.get(target.getUniqueId()).equals(mcPlayer.getUniqueId())) {
                                target.sendMessage(ChatColor.RED + mcPlayer.getName() + ChatColor.RED + " has rejected your teleport request.");
                                mcPlayer.sendMessage(ChatColor.RED + target.getName() + ChatColor.RED + "'s teleport request was rejected.");
                                SMP.pendingTeleports.remove(target.getUniqueId());
                                SMP.teleportRequests.remove(target.getUniqueId());
                                return true;
                            }
                        }
                    }
                    mcPlayer.sendMessage(ChatColor.RED + "That player did not request to teleport to you!");
                    break;
                default:
                    mcPlayer.sendMessage("Unknown command. Type \"/help\" for help.");
                    break;
            }
            return true;
        }
        return true;
    }
}