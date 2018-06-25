package network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ethan on 1/15/2017.
 */
public class TwitterEngine {

    public static Twitter t;
    static TwitterFactory tf;
    private static ConfigurationBuilder cb = new ConfigurationBuilder();

    public static void initializeTwitterFactory() {
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("T7xj0OPQEVTg7tbxCldJtqPzM")
                .setOAuthConsumerSecret(
                        "Zgxjngis5hrnWHEWGhPeDmcBKjHxezqfBIytlqN4vTRBHbuY9t")
                .setOAuthAccessToken(
                        "727951997501120512-fRIEAjm4szQsyoD7w5qQ2sUb0xwvasU")
                .setOAuthAccessTokenSecret(
                        "NPhWeWj7tLNbnxLdc4krR9OpPo19PcQEskQ0vkJrupYeH");
        tf = new TwitterFactory(cb.build());
        t = tf.getInstance();
        Bukkit.getLogger().warning("TwitterAuth // Twitter successfully authenticated.");
        //debug();
    }

    public static void postToTwitter(Player player, String tweet) throws TwitterException {
        Status s = t.updateStatus(tweet);
        Post.broadcastMessage(ChatColor.GRAY + "Dispatched " + ChatColor.AQUA + "Tweet" + ChatColor.GRAY + " with message \"" + ChatColor.YELLOW + s.getText() + ChatColor.GRAY + "\"");
        Bukkit.getLogger().info("Twitter4j // Sent tweet [" + s.getText() + "] from user [" + player.getName() + "]");
    }

    static void debug() {
        try {
            Status s = t.updateStatus("Test Tweet from UHCReborn Server");
            Bukkit.getLogger().info("Twitter4j//Tweet posted with message ["
                    + s.getText() + "]");
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

}
