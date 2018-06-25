package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.MathUtils;
import network.reborn.core.Util.Particles;
import network.reborn.core.Util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class ChristmasTree extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    int[] logColor = {101, 67, 33};
    private boolean active = false;
    private Location lastLocation;

    public ChristmasTree() {
        super("Christmas Tree", "christmas-tree", Material.LONG_GRASS, 0);
        setCooldown(20);
        Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                if (active) {
                    drawLog();
                    drawLeavesAndBalls();
                    drawStar();
                    drawSnow();
                }
            }
        }, 1L, 1L);
    }

    @Override
    public void doGadget(final Player player) {
        if (active)
            return;
        lastLocation = player.getLocation().add(0.5d, 0, 0.5d);
        active = true;
        Bukkit.getScheduler().runTaskLaterAsynchronously(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                active = false;
            }
        }, 200L);
    }

    private void drawSnow() {
        lastLocation.add(0, 3, 0);
        UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d, lastLocation, 10);
        lastLocation.subtract(0, 3, 0);
    }

    private void drawLog() {
        Location current = lastLocation.clone();
        Location to = lastLocation.clone().add(0, 2.5, 0);
        Vector link = to.toVector().subtract(current.toVector());
        float length = (float) link.length();
        link.normalize();
        float ratio = length / 10;
        Vector vector = link.multiply(ratio);
        for (int i = 0; i < 10; i++) {
            UtilParticles.display(logColor[0], logColor[1], logColor[2], current);
            current.add(vector);
        }
        current = null;
        to = null;
    }

    private void drawLeavesAndBalls() {
        float radius = 0.7f;
        for (float f = 0.8f; f <= 2.5f; f += 0.2f) {
            if (radius >= 0) {
                float d = 13f / f;
                float g = MathUtils.random(0, d);
                int e = MathUtils.random(0, 2);
                if (e == 1) {
                    double inc = (2 * Math.PI) / d;
                    float angle = (float) (g * inc);
                    float x = MathUtils.cos(angle) * (radius + 0.05f);
                    float z = MathUtils.sin(angle) * (radius + 0.05f);
                    lastLocation.add(x, f, z);
                    UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255), lastLocation);
                    lastLocation.subtract(x, f, z);
                }
                for (int i = 0; i < d; i++) {
                    double inc = (2 * Math.PI) / d;
                    float angle = (float) (i * inc);
                    float x = MathUtils.cos(angle) * radius;
                    float z = MathUtils.sin(angle) * radius;
                    lastLocation.add(x, f, z);
                    UtilParticles.display(0, 100, 0, lastLocation);
                    lastLocation.subtract(x, f, z);
                }
                radius = radius - (0.7f / 8.5f);
            }
        }
    }

    private void drawStar() {
        lastLocation.add(0, 2.6, 0);
        UtilParticles.display(255, 255, 0, lastLocation);
        lastLocation.subtract(0, 2.6, 0);
    }

}
