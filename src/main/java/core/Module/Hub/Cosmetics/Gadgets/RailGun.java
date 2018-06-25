package network.reborn.core.Module.Hub.Cosmetics.Gadgets;


import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.FireworkEffectPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RailGun extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();

    public RailGun() {
        super("Rail Gun", "rail-gun", Material.WOOD_HOE, 0);
        setCooldown(5);
    }

    @Override
    public void doGadget(final Player player) {
        final Location eyeLocation = player.getEyeLocation();
        final double px = eyeLocation.getX();
        final double py = eyeLocation.getY();
        final double pz = eyeLocation.getZ();
        final double yaw = Math.toRadians(eyeLocation.getYaw() + 90.0f);
        final double pitch = Math.toRadians(eyeLocation.getPitch() + 90.0f);
        final double x = Math.sin(pitch) * Math.cos(yaw);
        final double y = Math.sin(pitch) * Math.sin(yaw);
        final double z = Math.cos(pitch);
        for (int i = 1; i < 50; ++i) {
            final Location location = new Location(player.getWorld(), px + i * x, py + i * z, pz + i * y);
            if (location.getBlock().getType().isSolid()) {
                break;
            }
            try {
                FireworkEffectPlayer.playFirework(RebornCore.getRebornCore(), player.getWorld(), location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 2.0f);
    }

}
