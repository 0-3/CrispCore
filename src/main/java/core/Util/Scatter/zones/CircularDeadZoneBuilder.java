package network.reborn.core.Util.Scatter.zones;

import org.bukkit.Location;

public class CircularDeadZoneBuilder implements DeadZoneBuilder {

    private double m_radius;

    /**
     * Will build dead zones that are a circle of the radius given.
     *
     * @param radius the radius of the circle
     */
    public CircularDeadZoneBuilder(double radius) {
        m_radius = radius;
    }

    /**
     * @return the radius of the circle
     */
    public double getRadius() {
        return m_radius;
    }

    /**
     * @param radius the radius of the circle
     * @return this
     */
    public CircularDeadZoneBuilder setRadius(double radius) {
        m_radius = radius;
        return this;
    }

    /**
     * @param location the location of the centre of the circle
     * @return the dead zone
     */
    @Override
    public DeadZone buildForLocation(Location location) {
        return new CircularDeadZone(location, m_radius);
    }

    private class CircularDeadZone implements DeadZone {

        private final Location m_centre;
        private final double m_radiusSquared;

        /**
         * Creates a dead zone based on a centre location and a radius around it
         *
         * @param centre the centre of the circle
         * @param radius the radius of the circle
         */
        protected CircularDeadZone(Location centre, double radius) {
            //check all on the same plane
            centre = centre.clone();
            centre.setY(0);
            m_centre = centre;
            m_radiusSquared = radius * radius;
        }

        @Override
        public boolean isLocationAllowed(Location location) {
            if (!location.getWorld().equals(m_centre.getWorld())) {
                return true;
            }

            Location checkLocation = location.clone();
            checkLocation.setY(0);
            return checkLocation.distanceSquared(m_centre) > m_radiusSquared;
        }
    }
}
