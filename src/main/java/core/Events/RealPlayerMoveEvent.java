package network.reborn.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class RealPlayerMoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerMoveEvent event;
    private boolean canceled = false;

    public RealPlayerMoveEvent(PlayerMoveEvent event) {
        this.event = event;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PlayerMoveEvent getEvent() {
        return event;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Player getPlayer() {
        return event.getPlayer();
    }

}
