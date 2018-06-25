package network.reborn.core.Module.Games;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class MapConfig {
    private FileConfiguration configuration;
    private String displayName = "";
    private String version = "0.0.0";
    private String author = "";
    private Location spawn = null;
    private ArrayList<Location> spawnLocations = new ArrayList<>();

    public MapConfig(FileConfiguration configuration) {
        this.configuration = configuration;
        if (getConfiguration().isSet("Version"))
            version = getConfiguration().getString("Version");

        if (getConfiguration().isSet("Display Name"))
            displayName = ChatColor.translateAlternateColorCodes('&', getConfiguration().getString("Display Name"));

        if (getConfiguration().isSet("Author"))
            author = ChatColor.translateAlternateColorCodes('&', getConfiguration().getString("Author"));
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVersion() {
        return version;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public void setSpawnLocations(ArrayList<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

}
