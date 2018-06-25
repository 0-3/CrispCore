package network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia;

import net.dean.jraw.ApiException;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameManager;
import network.reborn.core.Module.Games.UltraHardcoreReddit.ScenariosAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOptionsAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ethan on 1/16/2017.
 */
public class Post implements Listener {
    public static HashMap<Player, Integer> stage = new HashMap<>();
    static HashMap<Player, Post> posts = new HashMap<>();

    public static void init(Player player) {
        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Started Match Post Maker.", player);
        //posts.put(player, this);
        stage.put(player, 1);
        if (GameManager.currentHost.length() < 2 || GameManager.currentHost.equalsIgnoreCase("None")) {
            UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Hmm... You still don't appear to have set the Host.", player);
            UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Please set the Host in " + ChatColor.BLUE + "Game Manager" + ChatColor.YELLOW + " before continuing.", player);
            posts.remove(player);
            stage.remove(player);
            UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Exited the Match Post Maker.", player);
        } else {
            Set<Player> players = new HashSet<Player>(Bukkit.getOnlinePlayers());
            Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, player, "Next", players));
        }
    }

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Match Post " + ChatColor.RESET + "" + ChatColor.GRAY + "» " + ChatColor.RESET + message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReceiveChat(AsyncPlayerChatEvent event) {
        if (stage.containsKey(event.getPlayer())) {
            final Player p = event.getPlayer();
            event.setCancelled(true);
            if (event.getMessage().equalsIgnoreCase("cancel") || event.getMessage().equalsIgnoreCase("no")) {
                posts.remove(p);
                stage.remove(p);
                UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Alright. Go ahead and make any changes to settings in the Game Menu, then restart this editor.", p);
                UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Exited the Match Post Maker.", p);
                return;
            } else if (event.getMessage().equalsIgnoreCase("done") && stage.get(p) == 0) {
                if (GameManager.currentHost.length() < 2 || GameManager.currentHost.equalsIgnoreCase("None")) {
                    UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Hmm... You still don't appear to have set the Host.", p);
                    UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Please set the Host in " + ChatColor.BLUE + "Game Manager" + ChatColor.YELLOW + " before continuing.", p);
                    posts.remove(p);
                    stage.remove(p);
                    UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Exited the Match Post Maker.", p);
                    return;
                }
                UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Great! Moving on...", p);
                stage.put(p, 1);
                Set<Player> players = new HashSet<Player>(Bukkit.getOnlinePlayers());
                Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, p, "Hi", players));
                return;
            } else if (event.getMessage().equalsIgnoreCase("yes")) {
                UltraHardcoreReddit.sendUHCMessage(ChatColor.GREEN + "Great!", p);
                int id = stage.get(p);
                stage.put(p, id + 1);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> {
                    Set<Player> players = new HashSet<Player>(Bukkit.getOnlinePlayers());
                    Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, p, "Next", players));
                }, 20L);
                return;
            } else {
                switch (stage.get(p)) {
                    case 0:
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Please set the Host in " + ChatColor.BLUE + "Game Manager" + ChatColor.YELLOW + " before continuing.", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Exited the Match Post Maker.", p);
                        stage.remove(p);
                        posts.remove(p);
                        break;
                    case 1:
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Now, I'm going to ask you some questions. Please pay attention and answer them.", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Are you ready? You may exit at any time by saying the word 'no' or 'cancel'.", p);
                        p.sendMessage(ChatColor.GRAY + "Please say " + ChatColor.GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.RED + "no");
                        break;
                    case 2:
                        String s = "";
                        try {
                            for (String t : ScenariosAPI.enabledScenariosInstance.keySet()) {
                                s = s + t + ", ";
                            }
                            s = s.substring(0, s.length() - 2);
                        } catch (StringIndexOutOfBoundsException e) {
                            s = "No Scenarios";
                        }
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Are these the Scenarios you want enabled?", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.GREEN + "Scenarios: " + ChatColor.AQUA + s, p);
                        p.sendMessage(ChatColor.GRAY + "Please say " + ChatColor.GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.RED + "no");
                        break;
                    case 3:
                        String s1 = "";
                        for (String t : UHCOptionsAPI.optionsInstance.keySet()) {
                            if (UHCOptionsAPI.isOptionEnabled(t)) {
                                s1 = s1 + t + ", ";
                            }
                        }
                        s1 = s1.substring(0, s1.length() - 2);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Are these the UHC Options you want enabled?", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.GREEN + "UHC Options: " + ChatColor.AQUA + s1, p);
                        p.sendMessage(ChatColor.GRAY + "Please say " + ChatColor.GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.RED + "no");
                        break;
                    case 4:
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Is this the slot count you want?", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.GREEN + "Slot Count: " + ChatColor.AQUA + GameManager.slots, p);
                        p.sendMessage(ChatColor.GRAY + "Please say " + ChatColor.GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.RED + "no");
                        break;
                    case 5:
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Now, for the final and hardest part, because I can't help you here.", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Please go to " + ChatColor.AQUA + "https://time.is/utc" + ChatColor.YELLOW + " and " + ChatColor.AQUA + "https://reddit.com/r/UHCMatches" + ChatColor.YELLOW + " to determine when to post your match, and what the UTC Time format will be.", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "The best I can provide is an example. Here you go:", p);
                        p.sendMessage(ChatColor.GRAY + "Jan 1 04:15 UTC");
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Note to use the month short code, so Jan for January, Feb for February, etc.", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Click this link for more info: " + ChatColor.AQUA + "https://goo.gl/AbXrHT", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "I have to assume the next thing you say is correctly formatted. So please, be careful.", p);
                        stage.put(p, 6);
                        break;
                    case 6:
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Alright! So now, assuming you did the previous part correctly, I'm now submitting this match post to Reddit and Twitter for you!", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Isn't technology great?", p);
                        UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Thank you, and good luck with your game!", p);
                        posts.remove(p);
                        stage.remove(p);
                        String msg = event.getMessage();
                        try {
                            RedditEngine.postToReddit(p, msg);
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

            }


        }
    }

}
