package network.reborn.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCombatEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean canceled = false;
    private boolean isCombat;

    public PlayerCombatEvent(Player player, boolean isCombat) {
        this.player = player;
        this.isCombat = isCombat;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCombat() {
        return this.isCombat;
    }

}
