package network.reborn.core.Module.Games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
    private Game game;

    public TeamCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!game.isTeams())
            return true;

        if (!(sender instanceof Player))
            return false;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /team <create/accept/invite/leave>");
            return true;
        }

        Team team = game.getPlayerTeam((Player) sender);

        switch (args[0].toLowerCase()) {
            default:
                sender.sendMessage(ChatColor.RED + "Correct Usage: /team <create/accept/invite/leave>");
                break;
            case "create":
                if (team != null) {
                    sender.sendMessage(ChatColor.RED + "You are already on a team");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Correct Usage: /team create <name>");
                    return true;
                } else if (args.length > 2) {
                    sender.sendMessage(ChatColor.RED + "Team names must be one word");
                    return true;
                }

                String teamName = args[1];
                if (game.getTeams().containsKey(teamName)) {
                    sender.sendMessage(ChatColor.RED + "Team name is already taken");
                    return true;
                }

                team = new Team(teamName, game);
                game.getTeams().put(teamName, team);
                team.addPlayer((Player) sender, true);
                sender.sendMessage(ChatColor.GREEN + "Created team " + teamName);
                break;
            case "invite":
                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "You are not currently on a team");
                    return true;
                }

                if (!team.getLeader().equals(((Player) sender).getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You are not the team leader");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Correct Usage: /team invite <player>");
                    return true;
                }
                if (team.getPlayers().size() + team.getPendingPlayers().size() >= game.getGameSettings().getTeamSize()) {
                    sender.sendMessage(ChatColor.RED + "You already have a full team (or have too many invites pending)!");
                    int total = team.getPlayers().size() + team.getPendingPlayers().size();
                    sender.sendMessage(ChatColor.RED + "Members: " + team.getPlayers().size() + " | Invited: " +
                            team.getPendingPlayers().size() + " | Total: " + total);
                    return true;
                }
                Player invited = Bukkit.getPlayer(args[1]);
                if (invited == null) {
                    sender.sendMessage(ChatColor.RED + "Player could not be found");
                    return true;
                }

                team.addPlayer(invited, false);
                invited.sendMessage(ChatColor.GREEN + "You have been invited to join the team " + team.getTeamColor() + team.getName());
                invited.sendMessage(ChatColor.GREEN + "Run \"/team accept " + team.getName() + "\" to accept");
                sender.sendMessage(ChatColor.GREEN + "Invited " + invited.getName() + " to join your team");
                break;
            case "accept":
                if (team != null) {
                    sender.sendMessage(ChatColor.RED + "You are already on a team");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Correct Usage: /team accept <team>");
                    return true;
                }

                teamName = args[1];
                Team invitedTo = game.getTeams().get(teamName);
                if (invitedTo == null) {
                    sender.sendMessage(ChatColor.RED + "Team was not found");
                    return true;
                }

                if (!invitedTo.getPendingPlayers().contains(((Player) sender).getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You have not been invited to this team");
                    return true;
                }

                invitedTo.addPlayer((Player) sender, false);
                sender.sendMessage(ChatColor.GREEN + "You have now joined the team " + invitedTo.getTeamColor() + invitedTo.getName());
                break;
            case "leave":
                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "You are not currently on a team");
                    return true;
                }

                team.removePlayer((Player) sender);
                sender.sendMessage(ChatColor.GREEN + "You have been removed from the team");
                if (team.getPlayers().size() == 0 && game.getTeams().containsKey(team.getName()))
                    game.getTeams().remove(team.getName()); // Remove team if no players are left in there
                break;
        }

        return true;
    }

}
