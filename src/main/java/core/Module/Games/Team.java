package network.reborn.core.Module.Games;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Team {
    private Game game;
    private String name = "";
    private UUID leader = null;
    private ChatColor teamColor = ChatColor.GRAY;
    private ArrayList<UUID> players = new ArrayList<>();
    private ArrayList<UUID> pendingPlayers = new ArrayList<>();
    private int kills = 0;
    private int deaths = 0;
    private int maxPlayers = 2;


    public Team(String name, Game game) {
        this.name = name;
        this.game = game;
    }

    public int getFreeSlots() {
        return maxPlayers - players.size() - pendingPlayers.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChatColor teamColor) {
        this.teamColor = teamColor;
    }

    public boolean addPlayer(Player player, boolean force) {
        return addPlayer(player.getUniqueId(), force);
    }

    public boolean addPlayer(UUID uuid, boolean force) {
        if (!force && !players.contains(uuid) && !pendingPlayers.contains(uuid)) {
            // Invite the player! Don't just join them!
            pendingPlayers.add(uuid);
            return true;
        }
        System.out.println(game.getPlayerTeams());
        game.getPlayerTeams().put(uuid, getName());
        System.out.println(game.getPlayerTeams());
        if (pendingPlayers.contains(uuid))
            pendingPlayers.remove(uuid);
        if (leader == null) {
            leader = uuid;
        }
        return getFreeSlots() >= 1 && players.add(uuid);
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        if (game.getPlayerTeams().containsKey(uuid))
            game.getPlayerTeams().remove(uuid);
        if (players.contains(uuid))
            players.remove(uuid);
        if (leader.equals(uuid) && players.size() > 0)
            leader = players.get(0);
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public double getKDR() {
        return getKills() / getDeaths();
    }

    public void doNametags() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                continue;
            NametagEdit.getApi().setPrefix(player, getTeamColor() + "");
        }
    }

    public UUID getLeader() {
        return leader;
    }

    public ArrayList<UUID> getPendingPlayers() {
        return pendingPlayers;
    }

}
