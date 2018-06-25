package network.reborn.core.Module.Hub.Cosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Hat extends Cosmetic {
    String hatPlayerName = null;
    short data = 0;

    public Hat(String name, String slug, Material material) {
        super(name, slug, CosmeticType.HAT, material);
    }

    public Hat(String name, String slug, Material material, String hatPlayerName) {
        super(name, slug, CosmeticType.HAT, material);
        this.hatPlayerName = hatPlayerName;
        if (material == Material.SKULL_ITEM)
            this.data = 3;
    }

    public Hat(String name, String slug, Material material, int cost) {
        super(name, slug, CosmeticType.HAT, material, cost);
    }

    public Hat(String name, String slug, Material material, int cost, String hatPlayerName) {
        super(name, slug, CosmeticType.HAT, material, cost);
        this.hatPlayerName = hatPlayerName;
    }

    public void setHat(Player player) {
        player.getInventory().setHelmet(getItemStack());
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial(), 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getName());
        itemStack.setItemMeta(itemMeta);
        if (getMaterial() == Material.SKULL_ITEM && hatPlayerName != null) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(hatPlayerName);
            itemStack.setItemMeta(skullMeta);
        }
        return itemStack;
    }
}
