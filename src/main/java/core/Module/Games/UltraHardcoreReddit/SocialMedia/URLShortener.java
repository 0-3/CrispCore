package network.reborn.core.Module.Games.UltraHardcoreReddit.SocialMedia;

import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ethan on 1/15/2017.
 */
public class URLShortener {

    private static String googUrl = "https://www.googleapis.com/urlshortener/v1/url?longUrl=http://goo.gl/fbsS&key=AIzaSyB8UACGgGVkBnyvEKVJw0o7nbntK_v1Bwo";

    public static String shorten(String longUrl) {
        String shortUrl = "";
        try {
            URLConnection conn = new URL(googUrl).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"longUrl\":\"" + longUrl + "\"}");
            wr.flush();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                if (line.indexOf("id") > -1) {
                    shortUrl = line.substring(8, line.length() - 2);
                    break;
                }
            }

            wr.close();
            rd.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Post.broadcastMessage(ChatColor.GRAY + "Created " + ChatColor.GREEN + "goo.gl link" + ChatColor.GRAY + " with URL " + ChatColor.YELLOW + shortUrl);
        return shortUrl;
    }

}
