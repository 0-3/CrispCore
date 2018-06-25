package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.lang.reflect.Field;

/**
 * Created by ethan on 5/28/2017.
 */
public class OceanRemovalSystem implements Listener {
    public OceanRemovalSystem() {
        System.out.println("=======================================");
        System.out.println("Jungle And Ocean Remover has been enabled");
        System.out.println("--------------------------------------");
        System.out.println("Biome Remapping will occur as follows:");
        System.out.println("DEEP_OCEAN -> PLAINS");
        System.out.println("OCEAN -> FOREST");
        System.out.println("JUNGLE -> FOREST");
        System.out.println("JUNGLE_EDGE -> FOREST");
        System.out.println("JUNGLE_HILLS -> FOREST");
        System.out.println("=======================================");
    }

    @EventHandler
    public void on(ChunkLoadEvent e) {
        Block b = e.getChunk().getBlock(e.getChunk().getX(), 100, e.getChunk().getZ());
        if ((b.getBiome().equals(Biome.JUNGLE)) ||
                (b.getBiome().equals(Biome.JUNGLE_EDGE)) ||
                (b.getBiome().equals(Biome.JUNGLE_EDGE_MOUNTAINS)) ||
                (b.getBiome().equals(Biome.JUNGLE_HILLS)) ||
                (b.getBiome().equals(Biome.JUNGLE_MOUNTAINS)) ||
                (b.getBiome().equals(Biome.OCEAN)) ||
                (b.getBiome().equals(Biome.DEEP_OCEAN))) {
            replaceChunks();
        }
    }

    public void replaceChunks() {
        Field biomesFiled = null;
        try {
            biomesFiled = BiomeBase.class.getDeclaredField("biomes");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        biomesFiled.setAccessible(true);
        try {
            if ((biomesFiled.get(null) instanceof BiomeBase[])) {
                BiomeBase[] biomes = (BiomeBase[]) biomesFiled.get(null);
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.FOREST;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
