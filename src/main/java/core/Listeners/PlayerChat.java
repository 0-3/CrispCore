package network.reborn.core.Listeners;

import network.reborn.core.API.DonorRank;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerChat implements Listener {
    public static HashMap<UUID, ChatColor> colors = new HashMap<UUID, ChatColor>();
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer().getUniqueId());

        if (!rebornPlayer.isLoadedDB()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Please wait for your profile to be loaded");
            return;
        }

        if (rebornPlayer.getServerRank() != ServerRank.DEFAULT) {
            if (colors.containsKey(rebornPlayer.getUUID())) {
                event.setFormat(rebornPlayer.getServerRank().getTabName() + colors.get(rebornPlayer.getUUID()) + rebornPlayer.getName() + ChatColor.GRAY + " » " + ChatColor.WHITE + "%2$s");
            } else {
                event.setFormat(rebornPlayer.getServerRank().getTabName() + ChatColor.AQUA + rebornPlayer.getName() + ChatColor.GRAY + " » " + ChatColor.WHITE + "%2$s");
            }
        } else if (rebornPlayer.getDonorRank() != DonorRank.DEFAULT) {
            event.setFormat(rebornPlayer.getDonorRank().getTabName() + ChatColor.WHITE + rebornPlayer.getName() + ChatColor.GRAY + " » " + ChatColor.WHITE + "%2$s");
        } else {
            event.setFormat(ChatColor.GRAY + rebornPlayer.getName() + ChatColor.GRAY + " » " + ChatColor.GRAY + "%2$s");
        }

        if (rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        } else {
            event.setMessage(event.getMessage().replaceAll("&[abcdefg1234567890lmnork]", ""));
        }

        event.setCancelled(true);

        /*(if (Post.stage.containsKey(event.getPlayer())) {
            return;
        }*/

        String[] words = event.getMessage().split(" ");
        for (Player player : event.getRecipients()) {
            boolean contains = false;
            String message = "";
            RebornPlayer rebornPlayer1 = RebornCore.getCoveAPI().getCovePlayer(player);
            for (String word : words) {
                if (word.replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()]", "").equalsIgnoreCase(rebornPlayer1.getName())) { //player mention
                    if (rebornPlayer1.isAfk()) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + rebornPlayer1.getName() + " is currently AFK");
                    }
                    message += ChatColor.YELLOW + word + " " + ChatColor.RESET;
                    contains = true;
                    continue;
                }
                message += word + " ";
            }
            if (contains && rebornPlayer1.isChatAlerts()) {
                player.sendMessage(event.getFormat().substring(0, event.getFormat().length() - 4) + message);
                player.playSound(player.getLocation(), Sound.ARROW_HIT, 1, 1);
            } else
                player.sendMessage(event.getFormat().substring(0, event.getFormat().length() - 4) + event.getMessage());
        }


//		for (String word : words) {
//			boolean shouldContinue = false;
//			for (Player player : event.getRecipients()) {
//				if (word.replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()]", "").equalsIgnoreCase(player.getName())) { //player mention
//					rebornPlayer = RebornCore.getRebornAPI().getCovePlayer(player);
//					if (rebornPlayer.isAfk()) {
//						event.getPlayer().sendMessage(ChatColor.YELLOW + player.getName() + " is currently AFK");
//					}
//					newMessage += ChatColor.YELLOW + word + " " + ChatColor.RESET;
//					shouldContinue = true;
//					break;
//				}
//			}
//			if (shouldContinue)
//				continue;
//			newMessage += word + " ";
//		}
//		event.setMessage(newMessage);

//		Bukkit.broadcastMessage(event.getFormat().substring(0, event.getFormat().length() - 4) + event.getMessage());

    }

}
