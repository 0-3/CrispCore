package network.reborn.core.Module.SMP.Listeners;

import network.reborn.core.Events.HelpCommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class HelpCommand implements Listener {

    @EventHandler
    public void onHelpCommand(HelpCommandEvent event) {
        HashMap<String, String> commands = new HashMap<>();
        commands.put("spawn", "Return to spawn");
        commands.put("home", "Visit/Manage your homes");
        commands.put("warp", "Teleport/View warps");
        commands.put("balance", "View your current balance");
        event.setCommands(commands);
    }

}
