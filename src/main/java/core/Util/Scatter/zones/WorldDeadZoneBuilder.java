package network.reborn.core.Util.Scatter.zones;

import org.bukkit.Location;
import org.bukkit.World;

public class WorldDeadZoneBuilder implements DeadZoneBuilder {

    private World m_world;

    /**
     * Creates deadzones for an entire world
     *
     * @param world the world to use
     */
    public WorldDeadZoneBuilder(World world) {
        m_world = world;
    }

    /**
     * @return the world that will be a deadzone
     */
    public World getWorld() {
        return m_world;
    }

    /**
     * @param world the world to make a dead zone
     * @return this
     */
    public WorldDeadZoneBuilder setWorld(World world) {
        m_world = world;
        return this;
    }

    /**
     * @param location the location, ignored as we have set parameters
     * @return the built deadzone
     */
    @Override
    public DeadZone buildForLocation(Location location) {
        return new WorldDeadZone(m_world);
    }


    private class WorldDeadZone implements DeadZone {

        private final World m_world;

        /**
         * Create a deadzone for an entire world
         *
         * @param world the world to make a dead zone
         */
        protected WorldDeadZone(World world) {
            m_world = world;
        }

        @Override
        public boolean isLocationAllowed(Location location) {
            return !location.getWorld().getName().equals(m_world.getName());
        }
    }
}
