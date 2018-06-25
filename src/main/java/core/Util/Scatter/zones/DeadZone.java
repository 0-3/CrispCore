package network.reborn.core.Util.Scatter.zones;

import org.bukkit.Location;

public interface DeadZone {

    /**
     * Does the location provided fall outside of our zone?
     *
     * @param location the location to check
     * @return true if outside zone, false otherwise
     */
    boolean isLocationAllowed(Location location);
}
