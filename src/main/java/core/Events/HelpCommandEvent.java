package network.reborn.core.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class HelpCommandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private HashMap<String, String> commands = new HashMap<>();
    private String[] args;
    private boolean canceled;

    public HelpCommandEvent(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }

    public HashMap<String, String> getCommands() {
        return commands;
    }

    public void setCommands(HashMap<String, String> commands) {
        this.commands = commands;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

}
