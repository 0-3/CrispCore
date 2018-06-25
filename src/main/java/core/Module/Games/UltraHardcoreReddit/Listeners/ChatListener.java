package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ethan on 1/14/2017.
 */
public class ChatListener implements Listener {
    public static Boolean enableChat = true;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!enableChat) {
            event.setCancelled(true);
        }
    }
}
