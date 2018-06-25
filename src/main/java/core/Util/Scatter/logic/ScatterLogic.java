package network.reborn.core.Util.Scatter.logic;

import network.reborn.core.Util.Scatter.exceptions.NoSolidBlockException;
import network.reborn.core.Util.Scatter.exceptions.ScatterLocationException;
import network.reborn.core.Util.Scatter.zones.DeadZone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class ScatterLogic {

    public static final double X_CENTRE = 0.5D;
    public static final double Z_CENTRE = 0.5D;
    private final Random m_random;

    public ScatterLogic(Random random) {
        m_random = random;
    }

    public Random getRandom() {
        return m_random;
    }

    /**
     * Gets the Z distance from the radius and angle, accurate to 2 decimal places using rounding mode ROUND_HALF_UP
     *
     * @param radius the radius
     * @param angle  the angle
     * @return the Z distance
     */
    public BigDecimal getZFromRadians(double radius, double angle) {
        BigDecimal zLength = new BigDecimal(StrictMath.cos(angle)).multiply(new BigDecimal(radius));
        return zLength.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Gets the X distance from the radius and angle, accurate to 2 decimal places using rounding mode ROUND_HALF_UP
     *
     * @param radius the radius
     * @param angle  the angle
     * @return the X distance
     */
    public BigDecimal getXFromRadians(double radius, double angle) {
        BigDecimal xLength = new BigDecimal(StrictMath.sin(angle)).multiply(new BigDecimal(radius));
        return xLength.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Sets the X and Z values to the nearest centre of a block (0.5,0.5)
     *
     * @param location the location to set
     */
    public void setToNearestCentre(Location location) {
        location.setX(StrictMath.floor(location.getX()) + X_CENTRE);
        location.setZ(StrictMath.floor(location.getZ()) + Z_CENTRE);
    }

    /**
     * Sets the Y coordinate to the highest non air block at a location, starting at it's Y and moving down
     *
     * @param loc The location to use
     * @throws NoSolidBlockException when there was no valid block found
     */
    public void setToHighestNonAir(Location loc) throws NoSolidBlockException {
        //Load the chunk first so the world is generated
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load(true);
        }

        Block block = loc.getBlock();
        while (block.getY() > 0) {
            //set the Y if we find a non-air block
            if (block.getType() != Material.AIR) {
                loc.setY(block.getY());
                return;
            }
            //keep falling down
            block = block.getRelative(BlockFace.DOWN);
        }

        //no non-air blocks were found all the way down
        throw new NoSolidBlockException();
    }

    /**
     * @param location  the location to check against
     * @param deadZones all of the deadzones to check
     * @return true if location is in deadzone, false otherwise
     */
    public boolean isLocationWithinDeadZones(Location location, Collection<DeadZone> deadZones) {
        for (DeadZone deadZone : deadZones) {
            if (!deadZone.isLocationAllowed(location)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a list of valid Scatter locations
     *
     * @param deadZones the list of zones for which spawning should be disallowed
     * @return a valid location
     * @throws ScatterLocationException on being not able to get a valid location
     */
    public abstract Location getScatterLocation(List<DeadZone> deadZones) throws ScatterLocationException;

    /**
     * @return the unique name of the Scatter logic
     */
    public abstract String getID();

    /**
     * @return a short description of how we Scatter
     */
    public abstract String getDescription();
}
