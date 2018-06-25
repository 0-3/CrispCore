package network.reborn.core.Module.Games;

import network.reborn.core.API.CoveServer;
import network.reborn.core.API.DonorRank;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {
    private String name;
    private String slug;
    private MapType mapType;
    private String downloadAddress = null;
    private MapConfig mapConfig = null;
    private World world = null;
    private String mapPath = "/home/minecraft/maps/";
    private ArrayList<Location> spawnCages = new ArrayList<>();
    private HashMap<String, Integer> spawns = new HashMap<>();

    public Map(String name, String slug, MapType mapType) {
        this.name = name;
        this.slug = slug;
        this.mapType = mapType;
    }

    public boolean loadMap() {
        // Check if already loaded
        world = Bukkit.getWorld("map-" + slug);
        if (world != null && mapConfig != null)
            return true;

        String path = mapPath + mapType.toString().toLowerCase() + "/" + slug;
        File localFile = new File(path);
        File localFileZip = new File(path + ".zip");
        System.out.println(path);
        System.out.println(path + ".zip");

        if (localFile.exists() && localFile.isDirectory()) {
            File srcFolder = new File(path);
            File destFolder = new File("map-" + slug);
            try {
                copyFolder(srcFolder, destFolder);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (localFileZip.exists()) {
            if (RebornCore.getCoveAPI().extractZip("/map-" + slug + ".zip", "/map-" + slug)) {
                if (new File("/map-" + slug).exists() && new File("/map-" + slug + "/config.yml").exists()) {
                    world = Bukkit.createWorld(new WorldCreator("map-" + slug));
                    mapConfig = new MapConfig(getMapConfig("/map-" + slug + "/config.yml"));
                    return true;
                }
            } else {
                return false;
            }
        } else if (getDownloadAddress() != null) {
            try {
                if (RebornCore.getCoveAPI().downloadFile(getDownloadAddress(), "/")) {
                    if (RebornCore.getCoveAPI().extractZip("/map-" + slug + ".zip", "/map-" + slug)) {
                        if (new File("map-" + slug).exists() && new File("map-" + slug + "/config.yml").exists()) {
                            world = Bukkit.createWorld(new WorldCreator("map-" + slug));
                            mapConfig = new MapConfig(getMapConfig("/map-" + slug + "/config.yml"));
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        // Load map and it's config such as spawn points
        if (new File("map-" + slug).exists() && new File("map-" + slug + "/config.yml").exists()) {
            if (world == null)
                world = Bukkit.createWorld(new WorldCreator("map-" + slug));
            mapConfig = new MapConfig(getMapConfig("map-" + slug + "/config.yml"));
            return true;
        }

        return false;
    }

    private FileConfiguration getMapConfig(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public MapType getMapType() {
        return mapType;
    }

    public String getDownloadAddress() {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }

    public World getWorld() {
        return world;
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    private void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public void tellDatabase() {
        CoveServer coveServer = RebornCore.getCoveAPI().getModule().getCoveServer();
        if (coveServer == null)
            return;
        JSONObject jsonObject = coveServer.getExtraData();
        jsonObject.put("Map", getName());
        coveServer.setExtraData(jsonObject);
    }

    public void resetMapOptions() {
        if (getWorld() == null)
            return;
        getWorld().setDifficulty(Difficulty.NORMAL);
        getWorld().setGameRuleValue("doDaylightCycle", "false");
        getWorld().setGameRuleValue("doMobSpawning", "false");
        getWorld().setGameRuleValue("doMobDrops", "true");
        getWorld().setGameRuleValue("keepInventory", "false");
        getWorld().setGameRuleValue("naturalRegeneration", "true");
        getWorld().setFullTime(6000);
        getWorld().setAutoSave(false);
    }

    public void loadSpawnCages() {
        if (!this.getMapConfig().getConfiguration().isSet("Spawn Blocks"))
            return;
        List<String> spawns = this.getMapConfig().getConfiguration().getStringList("Spawn Blocks");
        for (String spawn : spawns) {
            String[] spawnSplit = spawn.split("\\|");
            if (spawnSplit.length == 0)
                spawnSplit = spawn.split("-");
            double x = new Double(spawnSplit[0]);
            double y = new Double(spawnSplit[1]);
            double z = new Double(spawnSplit[2]);
            double yaw = 0;
            if (spawnSplit.length > 3)
                yaw = new Double(spawnSplit[3]);
            spawnCages.add(new Location(Bukkit.getWorld("map-" + this.getSlug()), x, y, z, (float) yaw, 0));
        }
    }

    public ArrayList<Location> getSpawnCages() {
        return spawnCages;
    }

    public void sendPlayerToSpawn(Player player) {
        int i = 0;
        for (Location spawn : spawnCages) {
            if (!spawns.containsValue(i)) {
                loadCage(player, spawn);
                player.teleport(spawn.clone().add(0.5, 1, 0.5));
                spawns.put(player.getName(), i);
                break;
            }
            i++;
        }
    }

    public int getSpawn(Player player) {
        return spawns.get(player.getName());
    }

    public void loadCage(Player player, Location baseBlock) {
        Material bottom = Material.GLASS;
        Material wall = Material.GLASS;
        Integer bottomData = 0;
        Integer wallData = 0;

        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        if (!rebornPlayer.isLoadedDB()) {
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> loadCage(player, baseBlock), 20L);
        } else if (rebornPlayer.canPlayer(ServerRank.HELPER)) {
            wall = Material.STAINED_GLASS_PANE;
            bottom = Material.STAINED_GLASS;
            bottomData = rebornPlayer.getServerRank().getBlockColor();
            wallData = rebornPlayer.getServerRank().getBlockColor();
        } else if (rebornPlayer.canPlayer(DonorRank.VIP)) {
            wall = Material.STAINED_GLASS_PANE;
            bottom = Material.STAINED_GLASS;
            bottomData = rebornPlayer.getDonorRank().getBlockColor();
            wallData = rebornPlayer.getDonorRank().getBlockColor();
        }
        loadCage(baseBlock, bottom, bottomData, wall, wallData);

    }

    public void loadCage(Location baseBlock, Material bottom, Integer bottomData, Material wall, Integer wallData) {
        ArrayList<Location> locations = new ArrayList<>();
        Location baseBlock1 = baseBlock.clone().add(1, 0, 1);
        locations.add(baseBlock1);
        Location baseBlock2 = baseBlock.clone().add(1, 0, 0);
        locations.add(baseBlock2);
        Location baseBlock3 = baseBlock.clone().add(0, 0, 1);
        locations.add(baseBlock3);
        Location baseBlock4 = baseBlock.clone().add(-1, 0, -1);
        locations.add(baseBlock4);
        Location baseBlock5 = baseBlock.clone().add(-1, 0, 0);
        locations.add(baseBlock5);
        Location baseBlock6 = baseBlock.clone().add(0, 0, -1);
        locations.add(baseBlock6);
        Location baseBlock7 = baseBlock.clone().add(1, 0, -1);
        locations.add(baseBlock7);
        Location baseBlock8 = baseBlock.clone().add(-1, 0, 1);
        locations.add(baseBlock8);

        ArrayList<Location> bottomLocations = (ArrayList<Location>) locations.clone();
        locations.clear();

        for (Location location : bottomLocations) {
            locations.add(location.clone().add(0, 1, 0));
            locations.add(location.clone().add(0, 2, 0));
            locations.add(location.clone().add(0, 3, 0));
        }

        for (Location location : bottomLocations) {
            location.getBlock().setType(bottom);
            if (bottomData != 0)
                location.getBlock().setData((byte) (int) bottomData);

            location.add(0, 4, 0);
            location.getBlock().setType(bottom);
            if (bottomData != 0)
                location.getBlock().setData((byte) (int) bottomData);
        }

        baseBlock.getBlock().setType(bottom);
        if (bottomData != 0)
            baseBlock.getBlock().setData((byte) (int) bottomData);

        Location topBlock = baseBlock.clone();
        topBlock.add(0, 4, 0);
        topBlock.getBlock().setType(bottom);
        if (bottomData != 0)
            topBlock.getBlock().setData((byte) (int) bottomData);

        for (Location location : locations) {
            location.getBlock().setType(wall);
            if (wallData != 0)
                location.getBlock().setData((byte) (int) wallData);
        }

    }

    public void removePlayerFromSpawns(Player player) {
        if (spawns.containsKey(player.getName()))
            spawns.remove(player.getName());
    }

    public void releaseCages() {
        for (Location location : spawnCages)
            loadCage(location, Material.AIR, 0, Material.AIR, 0);
    }

}
