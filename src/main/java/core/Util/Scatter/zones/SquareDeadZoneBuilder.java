package network.reborn.core.Util.Scatter.zones;

import org.bukkit.Location;

public class SquareDeadZoneBuilder implements DeadZoneBuilder {

    private double m_sideLength;

    /**
     * Creates deadzones with a side length
     *
     * @param sideLength the length of 1 side of the square
     */
    public SquareDeadZoneBuilder(double sideLength) {
        m_sideLength = sideLength;
    }

    /**
     * @return the length of 1 side of the square
     */
    public double getSideLength() {
        return m_sideLength;
    }

    /**
     * @param sideLength the length of 1 side of the square
     * @return this
     */
    public SquareDeadZoneBuilder setSideLength(double sideLength) {
        m_sideLength = sideLength;
        return this;
    }

    /**
     * @param location the centre of the square
     * @return the built deadzone
     */
    @Override
    public DeadZone buildForLocation(Location location) {
        return new SquareDeadZone(location, m_sideLength);
    }

    private class SquareDeadZone implements DeadZone {

        private final Location m_centre;
        private final double m_minX;
        private final double m_maxX;
        private final double m_minZ;
        private final double m_maxZ;

        /**
         * Creates a dead zone based on a centre location and a radius around it
         *
         * @param centre     the centre of the square
         * @param sideLength the length of 1 side of the square
         */
        protected SquareDeadZone(Location centre, double sideLength) {
            m_centre = centre;
            m_sideLength = sideLength;
            double radius = sideLength / 2.0D;
            m_minX = m_centre.getX() - radius;
            m_minZ = m_centre.getZ() - radius;
            m_maxX = m_centre.getX() + radius;
            m_maxZ = m_centre.getZ() + radius;
        }

        @Override
        public boolean isLocationAllowed(Location location) {
            return !location.getWorld().equals(m_centre.getWorld())
                    || location.getX() > m_maxX
                    || location.getX() < m_minX
                    || location.getZ() > m_maxZ
                    || location.getZ() < m_minZ;
        }
    }
}
