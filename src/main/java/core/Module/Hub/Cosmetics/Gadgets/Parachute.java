package network.reborn.core.Module.Hub.Cosmetics.Gadgets;

import network.reborn.core.Module.Hub.Cosmetics.Gadget;
import network.reborn.core.Util.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parachute extends Gadget {
    public HashMap<String, Integer> cooldownMap = new HashMap<>();
    List<Chicken> chickens = new ArrayList<>();

    public Parachute() {
        super("Parachute", "parachute", Material.LEASH, 0);
        setCooldown(10);
    }

    @Override
    public void doGadget(final Player player) {
        Location loc = player.getLocation();

        player.teleport(loc.clone().add(0, 35, 0));

        player.setVelocity(new Vector(0, 0, 0));

        for (int i = 0; i < 20; i++) {
            Chicken chicken = (Chicken) player.getWorld().spawnEntity(player.getLocation().add(MathUtils.randomDouble(0, 0.5), 3, MathUtils.randomDouble(0, 0.5)), EntityType.CHICKEN);
            chickens.add(chicken);
            chicken.setLeashHolder(player);
        }
    }

    private void killParachute(Player player) {
        for (Chicken chicken : chickens) {
            chicken.setLeashHolder(null);
            chicken.remove();
        }
        MathUtils.applyVelocity(player, new Vector(0, 0.15, 0));
    }

}
