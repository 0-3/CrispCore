package network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Submission;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Database.RedditDatabase;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameManager;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Scenario;
import network.reborn.core.Module.Games.UltraHardcoreReddit.ScenariosAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UHCOptionsAPI;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import twitter4j.TwitterException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ethan on 1/15/2017.
 */
public class RedditEngine {

    public static RedditDatabase db;
    static RedditClient rClient;
    static Credentials creds;

    public static void initializeRedditClient() throws OAuthException {
        db = new RedditDatabase();
        db.onEnable();
        UserAgent agent = UserAgent.of("desktop", "network.reborn.core.Modules.UltraHardcoreReddit.UltraHardcoreReddit", "v1.0", "RebornNetwork");
        rClient = new RedditClient(agent);
        creds = Credentials.script("RebornNetwork", "Dm1pZTE4KaeO", "QmPw7wIL1UFWoA", "MpQkwaaN6BZNLcQmtmqdPAGW8bY");
        OAuthData data = rClient.getOAuthHelper().easyAuth(creds);
        rClient.authenticate(data);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(RebornCore.getRebornCore(), () -> {
            Boolean worked = true;
            try {
                UserAgent agent2 = UserAgent.of("desktop", "network.reborn.core.Modules.UltraHardcoreReddit.UltraHardcoreReddit", "v1.0", "RebornNetwork");
                rClient = new RedditClient(agent2);
                creds = Credentials.script("RebornNetwork", "Dm1pZTE4KaeO", "QmPw7wIL1UFWoA", "MpQkwaaN6BZNLcQmtmqdPAGW8bY");
                OAuthData data2 = rClient.getOAuthHelper().easyAuth(creds);
                rClient.authenticate(data2);
            } catch (OAuthException e) {
                worked = false;
                e.printStackTrace();
            }
            if (worked) {
                Bukkit.getLogger().warning("RedditAuth // Reddit successfully authenticated.");
            }
        }, 0L, 36000L);
    }

    public static void disableRedditClient() {
        db.onDisable();
    }

    public static void postToReddit(Player executor, String matchTime) throws ApiException {
        String host = GameManager.currentHost;
        String[] scenarios = ScenariosAPI.enabledScenariosInstance.keySet().toArray(new String[ScenariosAPI.enabledScenariosInstance.size()]);
        String mapSize = "3000x3000";
        String pvp = "15 minutes in";
        String border = "Rescatters into 100x100 at 60 minutes in";
        Boolean goldenHeads = UHCOptionsAPI.isOptionEnabled("Golden Heads");
        Boolean nether = UHCOptionsAPI.isOptionEnabled("Nether");
        Boolean teams = RebornCore.getCoveAPI().getGame().getGameSettings().isTeams();
        String teamSize = "";
        if (RebornCore.getCoveAPI().getGame().getGameSettings().getTeamSize() == 1) {
            teamSize = "FFA";
        } else {
            teamSize = String.valueOf(RebornCore.getCoveAPI().getGame().getGameSettings().getTeamSize());
        }
        HashMap<String, String> scenarioTable = new HashMap<>();
        for (String s : scenarios) {
            Class<?> clazz = ScenariosAPI.scenarios.get(s);
            Object es = ScenariosAPI.enabledScenariosInstance.get(s);
            String method = clazz.getAnnotation(Scenario.class).getMenuItem();
            ItemStack item = new ItemStack(Material.DIRT);
            ItemMeta im = item.getItemMeta();
            ArrayList<String> dummy = new ArrayList<>();
            dummy.add("Uh oh! Something went wrong loading the ItemStack instance!");
            im.setLore(dummy);
            item.setItemMeta(im);
            try {
                item = (ItemStack) clazz.getMethod(method).invoke(es);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            String desc = "";
            StringBuilder b = new StringBuilder();
            ArrayList<String> lore = (ArrayList<String>) item.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                b.append(lore.get(i) + " ");
            }
            desc = b.toString();
            scenarioTable.put(s, desc);
        }
        if (host.length() < 1) {
            UltraHardcoreReddit.sendUHCMessage(ChatColor.RED + "Host cannot be empty.", executor);
            return;
        }
        build(executor, RebornCore.getRebornCore().getConfig().getString("directConnect"), String.valueOf(GameManager.slots), matchTime, teamSize, "3000", cb(nether), "Disabled", "Disabled", cb(goldenHeads), "Enabled", "Enabled", "Vanilla", "Vanilla", StringUtils.join(scenarioTable.keySet(), ","), scenarioTable);

    }

    public static void build(Player p, String ip, String slots, String matchTime, String teamSize, String mapSize, String nether, String tier2Pots, String strength, String goldenHeads, String absorption, String pearlDamage, String appleRates, String flintRates, String scenarioNames, HashMap<String, String> scenarioDescriptions) {
        FluentRedditClient client = new FluentRedditClient(rClient);
        Submission fin = null;
        try {
            ArrayList<String> url = new ArrayList<>();
            url.add("###[*Server Information*](#l)");
            url.add(" | |");
            url.add("---------|----------");
            url.add("**IP** | " + ip);
            url.add("**Direct IP** | mc.reborn.network:20002");
            url.add("**Slots** | " + slots);
            url.add("**Location** | Montreal CA");
            url.add("**RAM/Memory** | 16GB");
            url.add("**Version** | 1.8.x");
            url.add("");
            url.add("---");
            url.add("");
            url.add("###[*Match Information*](#l)");
            url.add(" | |");
            url.add("---------|----------");
            url.add("**Whitelist off** | " + matchTime);
            url.add("**Whitelist on** | When full OR 5 minutes after open.");
            url.add("**Team Size** | " + teamSize);
            url.add("**Map Size** | " + mapSize + "*" + mapSize);
            url.add("**PvP/iPvP** | 15 minutes in");
            url.add("**Duration** | 60 Minutes + Meetup");
            url.add("");
            url.add("---");
            url.add("");
            url.add("###[*Game Information*](#l)");
            url.add(" | |");
            url.add("---------|----------");
            url.add("**Nether** | " + nether);
            url.add("**Trapping** | Not Allowed");
            url.add("**Camping** | Not Allowed");
            url.add("**Potions** | Strength: " + strength + " [Tier 2: " + tier2Pots + "]");
            url.add("**Forts/Towers** | Not Allowed at Meetup.");
            url.add("**Golden Heads** | " + goldenHeads);
            url.add("**Absorption** | " + absorption);
            url.add("**Pearl Damage** | " + pearlDamage);
            if (appleRates.equalsIgnoreCase("Vanilla")) {
                url.add("**Apple Rates** | " + appleRates);
            } else {
                url.add("**Apple Rates** | " + appleRates + "%");
            }
            if (flintRates.equalsIgnoreCase("Vanilla")) {
                url.add("**Flint Rates** | " + flintRates);
            } else {
                url.add("**Flint Rates** | " + flintRates + "%");
            }
            url.add("**Stalking** | Allowed");
            url.add("**Stealing** | Allowed");
            url.add("");
            url.add("---");
            url.add("");
            url.add("###[*Scenario Information*](#l)");
            url.add("");
            url.add("Scenarios: " + scenarioNames);
            url.add("");
            url.add(" | |");
            url.add("---------|----------");
            for (String s : scenarioDescriptions.keySet()) {
                url.add("**" + s + "** | " + ChatColor.stripColor(scenarioDescriptions.get(s)));
            }
            url.add("");
            url.add("---");
            url.add("");
            String post = "";
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < url.size(); i++) {
                b.append(url.get(i) + "\n");
            }
            post = b.toString();
            //TODO: Change subreddit to UHCMatches
            fin = client.subreddit("uhcmatches").submit(post, matchTime + " NA - Reborn Network's #" + (RedditEngine.db.getLastUsedId() + 1) + " - " + teamSize + " - " + scenarioNames + " [Reborn]");
            Post.broadcastMessage(ChatColor.GRAY + "Posted to " + ChatColor.RED + "Reddit" + ChatColor.GRAY + " with submission URL " + ChatColor.GRAY + fin.getShortURL());
            UltraHardcoreReddit.postURL = fin.getShortURL();
        } catch (ApiException e) {
            Post.broadcastMessage(ChatColor.RED + "Error occured when posting to Reddit. Reason: " + ChatColor.GRAY + StringUtils.capitalize(e.getExplanation()));
        }
        if (fin != null) {
            try {
                TwitterEngine.postToTwitter(p, "Upcoming game!  IP: " + ip + "   Link: " + fin.getShortURL());
            } catch (TwitterException e) {
                Post.broadcastMessage(ChatColor.RED + "Error occured when posting to Twitter. Reason: " + ChatColor.GRAY + "[" + e.getErrorCode() + "] " + e.getErrorMessage());

            }
        }

    }

    static String cb(Boolean b) {
        if (b)
            return "Enabled";
        else
            return "Disabled";
    }


}
