package network.reborn.core.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FireworkEffectPlayer {
    private static Method world_getHandle;
    private static Method nms_world_broadcastEntityEffect;
    private static Method firework_getHandle;

    static {
        FireworkEffectPlayer.world_getHandle = null;
        FireworkEffectPlayer.nms_world_broadcastEntityEffect = null;
        FireworkEffectPlayer.firework_getHandle = null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void playFirework(final Plugin plugin, final World world, final Location loc) throws Exception {
        final Firework fw = (Firework) world.spawn(loc, (Class) Firework.class);
        if (FireworkEffectPlayer.world_getHandle == null) {
            FireworkEffectPlayer.world_getHandle = getMethod(world.getClass(), "getHandle");
            FireworkEffectPlayer.firework_getHandle = getMethod(fw.getClass(), "getHandle");
        }

        final Object nms_world = FireworkEffectPlayer.world_getHandle.invoke(world);
        final Object nms_firework = FireworkEffectPlayer.firework_getHandle.invoke(fw);
        if (FireworkEffectPlayer.nms_world_broadcastEntityEffect == null) {
            FireworkEffectPlayer.nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
        }

        final FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        fw.setFireworkMeta(data);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    FireworkEffectPlayer.nms_world_broadcastEntityEffect.invoke(nms_world, nms_firework, (byte) 17);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
        }.runTaskLater(plugin, 15L);
        fw.remove();
    }

    private static Method getMethod(final Class<?> cl, final String method) {
        Method[] methods;
        for (int length = (methods = cl.getMethods()).length, i = 0; i < length; ++i) {
            final Method m = methods[i];
            if (m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }
}