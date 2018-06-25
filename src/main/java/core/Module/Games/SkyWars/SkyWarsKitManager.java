package network.reborn.core.Module.Games.SkyWars;

import network.reborn.core.Module.Games.Game;
import network.reborn.core.Module.Games.Kit;
import network.reborn.core.Module.Games.KitManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SkyWarsKitManager extends KitManager {
    private Game game = null;

    public SkyWarsKitManager() {
        super();
        loadKits();
    }

    public SkyWarsKitManager(Game game) {
        super(game);
        loadKits();
    }

    protected String getSlug() {
        return "skywars";
    }

    protected void loadKits() {
        Kit defaultKit = new Kit("Default", "default", 0, getSlug(), new ItemStack(Material.WOOD_SWORD), new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE));

        ItemStack knightItem = new ItemStack(Material.WOOD_SWORD);
        knightItem.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        Kit knightKit = new Kit("Knight", "knight", 400, getSlug(), knightItem);

        Kit archerKit = new Kit("Archer", "archer", 400, getSlug(), new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 10));

        Kit armorer = new Kit("Armorer", "armorer", 400, getSlug());
        armorer.setHelmet(new ItemStack(Material.GOLD_HELMET));
        armorer.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        armorer.setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        armorer.setBoots(new ItemStack(Material.GOLD_BOOTS));

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        book.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        Kit enchanter = new Kit("Enchanter", "enchanter", 400, getSlug(), new ItemStack(Material.EXP_BOTTLE, 32), new ItemStack(Material.ANVIL), book);

        Kit pyro = new Kit("Pyro", "pyro", 400, getSlug(), new ItemStack(Material.TNT, 10), new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.GOLDEN_APPLE, 2));

        Kit enderman = new Kit("Enderman", "enderman", 400, getSlug(), new ItemStack(Material.ENDER_PEARL));
        ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestPlate.getItemMeta();
        meta.setColor(Color.BLACK);
        chestPlate.setItemMeta(meta);
        enderman.setChestplate(chestPlate);

//		Kit necromancer = new Kit("Necromancer", "necromancer", 400, getSlug()); // Add this once we can run commands on kit start

        if (game != null)
            addKit(defaultKit);
        addKit(knightKit);
        addKit(archerKit);
        addKit(armorer);
        addKit(enchanter);
        addKit(pyro);
        addKit(enderman);
        setDefaultKit(defaultKit);
    }


}
