package network.reborn.core.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {
    public static String locationAsString(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";"
                + location.getY() + ";" + location.getZ() + ";"
                + location.getPitch() + ";" + location.getYaw();
    }

    public static Location stringAsLocation(String string) {
        String[] parts = string.split(";");
        if (parts.length >= 4) {
            World world = Bukkit.getWorld(parts[0]);
            if (world != null) {
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);

                Location location = new Location(world, x, y, z);
                if (parts.length == 6) {
                    float pitch = Float.parseFloat(parts[4]);
                    float yaw = Float.parseFloat(parts[5]);
                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
                return location;
            }
        }
        return null;
    }
}
