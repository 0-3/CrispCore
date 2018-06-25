package network.reborn.core.Module.Games.UltraHardcoreReddit.Pregame.Parkour;

import org.bukkit.Material;

/**
 * Created by ethan on 12/20/2016.
 */
public enum ParkourTiers {

    EASY("Easy", Material.WOOL, 5), MEDIUM("Medium", Material.WOOL, 1), HARD("Hard", Material.WOOL, 14), EXTREME("Extreme", Material.WOOL, 15);

    private String n;
    private Material b;
    private int d;

    ParkourTiers(String name, Material blockMat, int blockDurability) {
        n = name;
        b = blockMat;
        d = blockDurability;
    }

    public String getName() {
        return n;
    }

    public Integer getDurability() {
        return d;
    }



}
