package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

/**
 * Created by ethan on 9/12/2016.
 */
public class GameData {
    private int i;
    private String u;
    private String wu;
    private String dt;
    private String s;
    private String p;

    public GameData(int id, String host_uuid, String datetime,
                    String scenarios, String matchURL) {
        i = id;
        u = host_uuid;
        dt = datetime;
        s = scenarios;
        p = matchURL;
    }

    public int getID() {
        return i;
    }

    public String getUUID() {
        return u;
    }

    public String getDateTime() {
        return dt;
    }

    public String getScenarios() {
        return s;
    }

    public String getMatchPostURL() {
        return p;
    }

}
