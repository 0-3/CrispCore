package network.reborn.core.API;

import java.util.UUID;

public class Booster {
    private UUID owner;
    private Long length;
    private BoosterType boosterType;
    private Module module = null;
    private boolean active = false;

    public Booster(RebornPlayer rebornPlayer, BoosterType boosterType, Long length) {
        this.owner = rebornPlayer.getUUID();
        this.boosterType = boosterType;
        this.length = length;
    }

    public Booster(RebornPlayer rebornPlayer, BoosterType boosterType, Long length, Module module) {
        this.owner = rebornPlayer.getUUID();
        this.boosterType = boosterType;
        this.length = length;
        this.module = module;
    }

    public UUID getOwner() {
        return owner;
    }

    public Long getLength() {
        return length;
    }

    public BoosterType getBoosterType() {
        return boosterType;
    }

    public Module getModule() {
        return module;
    }

    public void activateBooster() {
        if (isActive())
            return;
        switch (getBoosterType()) {
            case PLAYER:
                // Booster has been activated for this player only
                break;
        }
    }

    public boolean isActive() {
        return active;
    }

    public enum BoosterType {
        GLOBAL, MODULE, PLAYER
    }

}
