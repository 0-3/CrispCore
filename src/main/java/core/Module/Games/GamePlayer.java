package network.reborn.core.Module.Games;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.StatType;
import network.reborn.core.RebornCore;

import java.util.UUID;

public class GamePlayer extends RebornPlayer {
    private int kills = 0;
    private int deaths = 0;
    private int earnedCoins = 0;
    private boolean isSpectator = false;

    public GamePlayer(UUID uuid) {
        super(uuid);
    }

    public void addKill() {
        if (RebornCore.getCoveAPI().getGame().getGameSettings().isStats())
            RebornCore.getCoveAPI().getGame().addStats(getPlayer(), StatType.KILL, 1, null);
        kills++;
    }

    public int getKills() {
        return kills;
    }

    public void addDeath() {
        if (RebornCore.getCoveAPI().getGame().getGameSettings().isStats())
            RebornCore.getCoveAPI().getGame().addStats(getPlayer(), StatType.DEATH, 1, null);
        deaths++;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getKDR() {
        return getKills() / getDeaths();
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean isSpectator) {
        this.isSpectator = isSpectator;
    }

    public void addCoins(int coins) {
        this.earnedCoins = this.earnedCoins + coins;
    }

    public int getEarnedCoins() {
        return earnedCoins;
    }
}
