package network.reborn.core.Module.SMP.Handlers;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomesHandler {
    public static HashMap<UUID, HashMap<String, Location>> homes = new HashMap<>();

    public static void setHome(String name, Location location, Player player) {
        name = name.toLowerCase();
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        Float yaw = location.getYaw();
        Float pitch = location.getPitch();
        String locationStr = world + "|" + x + "|" + y + "|" + z + "|" + yaw + "|" + pitch;

        String query;
        if (checkHomeExists(name, player)) {
            deleteHome(name, player);
        }

        HashMap<String, Location> playerHomes = new HashMap<>();
        if (homes.containsKey(player.getUniqueId()))
            playerHomes = homes.get(player.getUniqueId());
        playerHomes.put(name, location);
        homes.put(player.getUniqueId(), playerHomes);

        query = "INSERT INTO `smp_homes` (`UUID`,`name`,`location`) VALUES ('" + player.getUniqueId().toString() + "','" + name + "','" + locationStr + "');";
        RebornCore.getCoveAPI().runSQLQueryPriority(query);
    }

    public static String getHomes(Player player) {
        if (homes.containsKey(player.getUniqueId())) {
            String response = "";
            for (Map.Entry<String, Location> entry : homes.get(player.getUniqueId()).entrySet()) {
                response += entry.getKey() + ", ";
            }
            if (response.equalsIgnoreCase(""))
                return "";
            return response.substring(0, response.length() - 2);
        }
        return "";
    }

    public static void deleteHome(String name, Player player) {
        if (homes.containsKey(player.getUniqueId())) {
            if (homes.get(player.getUniqueId()).containsKey(name))
                homes.get(player.getUniqueId()).remove(name);
        }
        String uuid = player.getUniqueId().toString();
        String query = "DELETE FROM `smp_homes` WHERE `name` = '" + name + "' AND `UUID` = '" + uuid + "';";
        RebornCore.getCoveAPI().runSQLQueryPriority(query);
    }

    public static boolean checkHomeExists(String name, Player player) {
        if (!homes.containsKey(player.getUniqueId()))
            return false;
        HashMap<String, Location> playerHomes = homes.get(player.getUniqueId());
        return playerHomes.containsKey(name);
    }

    public static void teleportPlayer(String name, Player player) {
        if (homes.containsKey(player.getUniqueId())) {
            if (homes.get(player.getUniqueId()).containsKey(name))
                player.teleport(homes.get(player.getUniqueId()).get(name));
        }

    }

    public static void teleportPlayerToFirstHome(Player player) {
        if (homes.containsKey(player.getUniqueId())) {
            for (Map.Entry<String, Location> entry : homes.get(player.getUniqueId()).entrySet()) {
                player.teleport(entry.getValue());
                return;
            }
        }
    }

    public static Integer getHomesLimit(Player player) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        switch (rebornPlayer.getDonorRank()) {
            default:
                return 3;
            case VIP:
                return 6;
            case VIPPLUS:
                return 9;
            case REBORN:
                return 12;
        }
    }

    public static Integer getCurrentHomesCount(Player player) {
        if (homes.containsKey(player.getUniqueId()))
            return homes.get(player.getUniqueId()).size();
        return 0;
    }

}
