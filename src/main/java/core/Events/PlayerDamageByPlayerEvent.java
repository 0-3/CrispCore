package network.reborn.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDamageByPlayerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Player damager;
    private boolean canceled = false;

    public PlayerDamageByPlayerEvent(Player player, Player damager) {
        this.player = player;
        this.damager = damager;
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

    public Player getDamager() {
        return damager;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

}
