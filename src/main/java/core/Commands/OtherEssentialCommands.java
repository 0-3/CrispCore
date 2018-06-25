package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OtherEssentialCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command must be ran as a player");
            return true;
        }

        Player player = (Player) sender;
        switch (string.toLowerCase()) {
            case "tppos":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Correct Usage: /tppos <x> <y> <z>");
                } else {
                    RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player.getUniqueId());
                    try {
                        double x = Double.parseDouble(args[0]);
                        double y = Double.parseDouble(args[1]);
                        double z = Double.parseDouble(args[2]);
                        rebornPlayer.doNiceTeleport(x, y, z, true);
                        player.sendMessage(ChatColor.GRAY + "Teleporting...");
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Please enter valid numbers");
                    }
                }
                break;
            case "gm1":
            case "gmc":
                if (args.length < 1) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                            + ChatColor.YELLOW + "Creative");
                } else {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p != null) {
                        p.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                + " has been set to gamemode " + ChatColor.YELLOW + "Creative");
                        p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                + ChatColor.YELLOW + "Creative");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    }
                }
                break;
            case "gm0":
            case "gms":
                if (args.length < 1) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                            + ChatColor.YELLOW + "Survival");
                } else {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p != null) {
                        p.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                + " has been set to gamemode " + ChatColor.YELLOW + "Survival");
                        p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                + ChatColor.YELLOW + "Survival");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    }
                }
                break;
            case "gm2":
            case "gma":
                if (args.length < 1) {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                            + ChatColor.YELLOW + "Adventure");
                } else {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p != null) {
                        p.setGameMode(GameMode.ADVENTURE);
                        player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                + " has been set to gamemode " + ChatColor.YELLOW + "Adventure");
                        p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                + ChatColor.YELLOW + "Adventure");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    }
                }
                break;
            case "gmsp":
            case "gm3":
                if (args.length < 1) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                            + ChatColor.YELLOW + "Spectator");
                } else {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p != null) {
                        p.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                + " has been set to gamemode " + ChatColor.YELLOW + "Spectator");
                        p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                + ChatColor.YELLOW + "Spectator");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    }
                }
                break;
            case "gamemode":
            case "gm":
                if (args.length == 1) {
                    switch (args[0]) {
                        default:
                            player.sendMessage(ChatColor.RED + "Correct usage: /gm <Gamemode> [Player]");
                        case "0":
                        case "s":
                        case "survival":
                            player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                    + ChatColor.YELLOW + "Survival");
                            player.setGameMode(GameMode.SURVIVAL);
                            break;
                        case "1":
                        case "c":
                        case "creative":
                            player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                    + ChatColor.YELLOW + "Creative");
                            player.setGameMode(GameMode.CREATIVE);
                            break;
                        case "2":
                        case "a":
                        case "adventure":
                            player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                    + ChatColor.YELLOW + "Adventure");
                            player.setGameMode(GameMode.ADVENTURE);
                            break;
                        case "3":
                        case "sp":
                        case "specator":
                            player.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                    + ChatColor.YELLOW + "Spectator");
                            player.setGameMode(GameMode.SPECTATOR);
                            break;
                    }
                } else if (args.length == 2) {
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p != null) {
                        switch (args[0]) {
                            default:
                                player.sendMessage(ChatColor.RED + "Correct usage: /gm <Gamemode> [Player]");
                            case "0":
                            case "s":
                            case "survival":
                                player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                        + " has been set to gamemode " + ChatColor.YELLOW + "Survival");
                                p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                        + ChatColor.YELLOW + "Survival");
                                p.setGameMode(GameMode.SURVIVAL);
                                break;
                            case "1":
                            case "c":
                            case "creative":
                                player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                        + " has been set to gamemode " + ChatColor.YELLOW + "Creative");
                                p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                        + ChatColor.YELLOW + "Creative");
                                p.setGameMode(GameMode.CREATIVE);
                                break;
                            case "2":
                            case "a":
                            case "adventure":
                                player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                        + " has been set to gamemode " + ChatColor.YELLOW + "Adventure");
                                p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                        + ChatColor.YELLOW + "Adventure");
                                p.setGameMode(GameMode.ADVENTURE);
                                break;
                            case "3":
                            case "sp":
                            case "specator":
                                player.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY
                                        + " has been set to gamemode " + ChatColor.YELLOW + "Spectator");
                                p.sendMessage(ChatColor.GRAY + "Your gamemode has been set to "
                                        + ChatColor.YELLOW + "Spectator");
                                p.setGameMode(GameMode.SPECTATOR);
                                break;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                    }
                }
                break;
            case "feed":
                if (args.length < 1) {
                    player.setFoodLevel(20);
                } else {
                    Player player1 = Bukkit.getPlayer(args[0]);
                    if (player1 == null) {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    } else {
                        player1.setFoodLevel(20);
                    }
                }
                break;
            case "heal":
                if (args.length < 1) {
                    player.setHealth(player.getMaxHealth());
                } else {
                    Player player1 = Bukkit.getPlayer(args[0]);
                    if (player1 == null) {
                        player.sendMessage(ChatColor.RED + "Player not found");
                    } else {
                        player1.setHealth(player.getMaxHealth());
                    }
                }
                break;
            case "sun":
            case "toggledownfall":
            case "tdf":
                Bukkit.getServer().getWorld(player.getWorld().toString()).setStorm(false);
                Bukkit.getServer().getWorld(player.getWorld().toString()).setThundering(false);
                player.sendMessage(ChatColor.GRAY + "Weather has been cleared.");
                break;
            case "speed":
                if (args.length < 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /speed <speed>");
                } else {
                    if (player.isFlying()) {
                        player.setFlySpeed(Float.parseFloat(args[0]));
                    } else {
                        player.setWalkSpeed(Float.parseFloat(args[0]));
                    }
                }
                break;
            case "ptime":
                if (args.length < 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /ptime <time>");
                } else {
                    if (args[0].equalsIgnoreCase("reset")) {
                        player.resetPlayerTime();
                    } else {
                        player.setPlayerTime(Long.parseLong(args[0]), false);
                    }
                }
                break;
        }
        return true;
    }

}
