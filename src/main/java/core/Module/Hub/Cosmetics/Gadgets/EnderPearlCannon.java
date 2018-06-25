package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EnderPearlCannon extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public EnderPearlCannon() {
        super("Ender Pearl Cannon", "ender-pearl-cannon", Material.GOLD_BARDING, 0);
        setCooldown(10);
    }

    @Override
    public void doGadget(final Player player) {
        player.launchProjectile(EnderPearl.class);
    }

}
