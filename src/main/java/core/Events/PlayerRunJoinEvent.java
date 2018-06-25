package network.reborn.core.Events;

import network.reborn.core.API.RebornPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRunJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final RebornPlayer player;
    private boolean canceled = false;

    public PlayerRunJoinEvent(RebornPlayer player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public RebornPlayer getPlayer() {
        return player;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

}
