package network.reborn.core.Util;

import org.bukkit.entity.Player;

public class DirectionUtils {
    public static Direction getCardinalDirection(final Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 67.5) {
            return Direction.NORTH;
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return Direction.EAST;
        }
        if (157.5 <= rotation && rotation < 247.5) {
            return Direction.SOUTH;
        }
        if (247.5 <= rotation && rotation < 337.5) {
            return Direction.WEST;
        }
        return Direction.NORTH;
    }

    public enum Direction {
        NORTH(),
        EAST(),
        WEST(),
        SOUTH()
    }
}