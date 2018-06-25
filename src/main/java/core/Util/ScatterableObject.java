package network.reborn.core.Util;

import network.reborn.core.Module.Games.Team;
import org.bukkit.OfflinePlayer;

public class ScatterableObject {

    Team st = null;
    OfflinePlayer sp = null;

    public ScatterableObject(OfflinePlayer p) {
        sp = p;
    }

    public ScatterableObject(Team t) {
        st = t;
    }

    public Boolean isPlayer() {
        return sp != null;
    }

    public Boolean isTeam() {
        return st != null;
    }

    public Team getTeam() {
        return st;
    }

    public OfflinePlayer getPlayer() {
        return sp;
    }

}
