package network.reborn.core.Module.Games;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {
    private Game game;

    public GameCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.HELPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /game <start/end/status/kit/pvp/menu> [other args...]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            default:
                sender.sendMessage(ChatColor.RED + "Unknown arg");
                break;
            case "menu":
                if (!RebornCore.getCoveAPI().getModule().getCoveServer().getModule().getNiceName().contains("Reddit")) {
                    sender.sendMessage(ChatColor.RED + "Game Menu is only enabled on Reddit UHC!");
                    break;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This can only be run as a player!");
                    break;
                }
                GameMenu.openMenu((Player) sender);
                break;
            case "meetup":
                if (!RebornCore.getCoveAPI().getModule().getCoveServer().getModule().getNiceName().contains("Reddit")) {
                    sender.sendMessage(ChatColor.RED + "Force-Meetup is only enabled on Reddit UHC!");
                    break;
                }
                /*if (UltraHardcoreReddit.inc < 900) {
                    sender.sendMessage(ChatColor.RED + "PvP has not been enabled yet. Meetup cannot be started less than 15 minutes into the game.");
                    break;
                }*/
                sender.sendMessage(ChatColor.RED + "Force-starting Meetup...");
                ((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).executeDeathmatch();

            case "setposturl":
                ((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).setPostURL(args[1]);
            case "teams":
                game.setTeams(!game.isTeams());
                break;
            case "status":
                sender.sendMessage(ChatColor.AQUA + "Game Title: " + game.getGameTitle());
                sender.sendMessage(ChatColor.AQUA + "Game ID: " + game.getGameID());
                sender.sendMessage(ChatColor.AQUA + "Game State: " + game.getGameState().toString());
                break;
            case "start":
                if (game.getGameState() == GameState.INGAME) {
                    sender.sendMessage(ChatColor.RED + "Game is already in progress");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Started game");
                    game.startGame();
                }
                break;
            case "end":
                if (game.getGameState() != GameState.INGAME) {
                    sender.sendMessage(ChatColor.RED + "Game is not in progress");
                } else {
                    String winner = "";
                    if (args.length >= 2) {
                        winner = ChatColor.translateAlternateColorCodes('&', args[1]);
                    }
                    sender.sendMessage(ChatColor.GREEN + "Ended game with winner \"" + winner + "\"");
                    game.endGame(winner);
                }
                break;
            case "kit":
                if (sender instanceof Player)
                    game.kitManager.openKitGUI((Player) sender);
                break;
            case "pvp":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Correct usage: /game pvp <mode> - Modes are PVP_1_9, PVP_1_8 or TRUE_1_8");
                } else {
                    try {
                        PVPMode pvpMode = PVPMode.valueOf(args[1].toUpperCase());
                        game.changePVPMode(pvpMode);
                        sender.sendMessage(ChatColor.GREEN + "PVP mode updated to " + pvpMode.toString());
                    } catch (Exception ignored) {
                        sender.sendMessage(ChatColor.RED + "Invalid PVP mode - Modes are PVP_1_9, PVP_1_8 or TRUE_1_8");
                    }
                }
        }

        return true;
    }

}
