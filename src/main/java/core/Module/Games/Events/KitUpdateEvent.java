package network.reborn.core.Module.Games.Events;

import network.reborn.core.Module.Games.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private Kit kit;

    public KitUpdateEvent(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
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

    public Kit getKit() {
        return kit;
    }

}
