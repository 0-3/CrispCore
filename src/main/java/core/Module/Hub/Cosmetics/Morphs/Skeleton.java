package network.reborn.core.Module.Hub.Cosmetics.Morphs;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import network.reborn.core.Module.Hub.Cosmetics.Morph;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Skeleton extends Morph {

    public Skeleton(String name, String slug, Material material, DisguiseType disguiseType) {
        super(name, slug, material, disguiseType);
        setSkillItem(new ItemStack(Material.BONE));
        setSkillMaterial(Material.BONE);
    }

    @Override
    public void doSkill(Player player) {
        player.sendMessage("Doing Skill..");
        player.launchProjectile(Arrow.class);
    }

}
