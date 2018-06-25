package network.reborn.core.Util.Scatter;

import network.reborn.core.Util.Scatter.exceptions.ScatterLocationException;
import network.reborn.core.Util.Scatter.logic.ScatterLogic;
import network.reborn.core.Util.Scatter.zones.DeadZone;
import network.reborn.core.Util.Scatter.zones.DeadZoneBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Scatterer {

    /**
     * Attempt to find safe locations. Returns the block that can be teleported onto, may require an offset
     *
     * @return list of locations outside of provided deadzones and outside of zones created by the builder
     * @throws ScatterLocationException if a location wasn't able to be fetched
     */
    List<Location> getScatterLocations(int amount) throws ScatterLocationException;

    /**
     * Scatter the players provided.
     * <p>Teleports players after fetching all the valid locations</p>
     *
     * @param players the players to Scatter
     * @throws ScatterLocationException
     */
    void scatterPlayers(List<Player> players) throws ScatterLocationException;

    ScatterLogic getLogic();

    void setLogic(ScatterLogic logic);

    DeadZoneBuilder getDeadZoneBuilder();

    void setDeadZoneBuilder(DeadZoneBuilder builder);

    List<DeadZone> getBaseDeadZones();

    void setBaseDeadZones(List<DeadZone> zones);

    void addBaseDeadZone(DeadZone zone);

    void clearBaseDeadZones();

    /**
     * Adds a base deadzone for every player provided using the DeadZoneBuilder
     *
     * @param players the player to create deadzones for
     */
    void addDeadZonesForPlayers(List<Player> players);

    /**
     * Adds a base deadzone for every player online that isn't in the list provided using the DeadZoneBuilder
     *
     * @param players the players to not create a deadzone for
     */
    void addDeadZonesForPlayersNotInList(List<Player> players);
}
