package network.reborn.core.Util.Scatter.logic;

import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public abstract class StandardScatterLogic extends ScatterLogic {

    protected int m_maxAttempts;
    protected double m_radius;
    protected Location m_centre;
    protected Set<Material> m_materials = Sets.newHashSet();

    public StandardScatterLogic(Random random, Location centre, int maxAttempts, double radius, Material... allowedMaterials) {
        super(random);
        m_maxAttempts = maxAttempts;
        m_radius = radius;
        m_centre = centre;
        m_materials.addAll(Arrays.asList(allowedMaterials));
    }

    public StandardScatterLogic(Random random) {
        super(random);
    }

    public int getMaxAttempts() {
        return m_maxAttempts;
    }

    public StandardScatterLogic setMaxAttempts(int attempts) {
        m_maxAttempts = attempts;
        return this;
    }

    public double getRadius() {
        return m_radius;
    }

    public StandardScatterLogic setRadius(double radius) {
        m_radius = radius;
        return this;
    }

    public Location getCentre() {
        return m_centre;
    }

    public StandardScatterLogic setCentre(Location centre) {
        m_centre = centre;
        return this;
    }

    public Set<Material> getMaterials() {
        return m_materials;
    }

    public StandardScatterLogic setMaterials(Set<Material> materials) {
        m_materials = materials;
        return this;
    }

    public StandardScatterLogic addMaterials(Material... materials) {
        m_materials.addAll(Arrays.asList(materials));
        return this;
    }
}
