package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player s = (Player) sender;
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(s.getUniqueId());
            //TODO Add TP Support Per Module - Limit To hosts/specs only
            if (rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                if (args.length == 2) {
                    if (Bukkit.getServer().getPlayer(args[0]) != null && Bukkit.getServer().getPlayer(args[1]) != null) {
                        Player t = Bukkit.getServer().getPlayer(args[0]);
                        Player p = Bukkit.getServer().getPlayer(args[1]);
                        if (t.getName() == p.getName()) {
                            s.sendMessage(ChatColor.RED + "You can not teleport a player to themselves!");
                            return true;
                        } else if (t.getName() == s.getName()) {
                            Location pL = p.getLocation();
                            s.teleport(pL);
                            s.sendMessage(ChatColor.GRAY + "Teleporting to " + ChatColor.YELLOW + p.getName()
                                    + ChatColor.GRAY + "!");
                            return true;
                        } else if (p.getName() == s.getName()) {
                            Location sL = s.getLocation();
                            t.teleport(sL);
                            s.sendMessage(ChatColor.GRAY + "Teleporting " + ChatColor.YELLOW + t.getName()
                                    + ChatColor.GRAY + " to you!");
                            t.sendMessage(ChatColor.YELLOW + s.getName()
                                    + ChatColor.GRAY + " is teleporting you to themself!");
                            return true;
                        } else {
                            Location pl = p.getLocation();
                            t.teleport(pl);
                            s.sendMessage(ChatColor.GRAY + "Teleporting " + ChatColor.YELLOW + t.getName()
                                    + ChatColor.GRAY + " to " + ChatColor.YELLOW + p.getName()
                                    + ChatColor.GRAY + "!");
                            t.sendMessage(ChatColor.YELLOW + s.getName()
                                    + ChatColor.GRAY + " is teleporting you to "
                                    + ChatColor.YELLOW + p.getName());
                            p.sendMessage(ChatColor.YELLOW + s.getName()
                                    + ChatColor.GRAY + " is teleporting " + ChatColor.YELLOW + t.getName()
                                    + ChatColor.GRAY + " to you!");
                            return true;
                        }
                    } else {
                        s.sendMessage(ChatColor.RED + "Player(s) not found!");
                    }
                } else if (args.length == 1) {
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        Player t = Bukkit.getServer().getPlayer(args[0]);
                        if (t.getName() == s.getName()) {
                            s.sendMessage(ChatColor.RED + "You can not teleport to yourself!");
                            return true;
                        } else {
                            Location tL = t.getLocation();
                            s.teleport(tL);
                            s.sendMessage(ChatColor.GRAY + "Teleporting to " + ChatColor.YELLOW + t.getName()
                                    + ChatColor.GRAY + "!");
                            return true;
                        }
                    } else {
                        s.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /" + label + " <player> [to player]");
                    return true;
                }
            } else if (rebornPlayer.canPlayer(ServerRank.HELPER)) {
                if (args.length == 1) {
                    if (Bukkit.getServer().getPlayer(args[0]) != null) {
                        Player t = Bukkit.getServer().getPlayer(args[0]);
                        if (t.getName() == s.getName()) {
                            s.sendMessage(ChatColor.RED + "You can not teleport to yourself!");
                            return true;
                        } else {
                            Location tL = t.getLocation();
                            s.teleport(tL);
                            s.sendMessage(ChatColor.GRAY + "Teleporting to " + ChatColor.YELLOW + t.getName()
                                    + ChatColor.GRAY + "!");
                            return true;
                        }
                    } else {
                        s.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /" + label + " <player>");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }
        return true;
    }
}