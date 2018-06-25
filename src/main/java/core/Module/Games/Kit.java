package network.reborn.core.Module.Games;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;

public class Kit {
    private String name;
    private String slug;
    private String gameSlug;
    private int price = 0;
    private ItemStack helmet = null;
    private ItemStack chestplate = null;
    private ItemStack leggings = null;
    private ItemStack boots = null;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private ArrayList<PotionEffectType> effects = new ArrayList<>();

    public Kit(String name, String slug, int price, String gameSlug, ItemStack... items) {
        this.name = name;
        this.slug = slug.replaceAll(" ", "-").replaceAll("_", "-"); // Don't allow spaces or underscores
        this.price = price;
        this.gameSlug = gameSlug;
        addItems(items);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    public String getPermission() {
        return "legacymc.kit." + getGameSlug() + "." + getSlug();
    }

    public void addItems(ItemStack... items) {
        Collections.addAll(this.items, items);
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemStack> items) {
        this.items = items;
    }

    public void addEffects(PotionEffectType... effects) {
        Collections.addAll(this.effects, effects);
    }

    public ArrayList<PotionEffectType> getEffects() {
        return effects;
    }

    public void setEffects(ArrayList<PotionEffectType> effects) {
        this.effects = effects;
    }

    public boolean playerHas(Player player) {
        if (getPrice() <= 0)
            return true;
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        return rebornPlayer.hasPermission(getPermission());
    }

    public boolean playerCanAfford(Player player) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        return rebornPlayer.getBalance(getGameSlug()) >= getPrice();
    }

    public boolean playerBuy(Player player) {
        if (!playerCanAfford(player))
            return false;
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        if (getPrice() <= 0) {
            rebornPlayer.givePermission(getPermission());
            return true;
        }
        rebornPlayer.takeBalance(getGameSlug(), getPrice());
        rebornPlayer.givePermission(getPermission());
        return true;
    }

    public void playerGive(Player player) {
        if (player == null || !player.isOnline() || !playerHas(player))
            return;

        // Clear inventory and Armor
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (!getItems().isEmpty()) {
            for (ItemStack itemStack : getItems()) {
                player.getInventory().addItem(itemStack);
            }
        }

        if (!getEffects().isEmpty()) {
            // TODO Improve potion effect system for different levels
            for (PotionEffectType potionEffectType : getEffects()) {
                player.addPotionEffect(new PotionEffect(potionEffectType, Integer.MAX_VALUE, 0));
            }
        }

        if (getHelmet() != null)
            player.getInventory().setHelmet(getHelmet());

        if (getChestplate() != null)
            player.getInventory().setChestplate(getChestplate());

        if (getLeggings() != null)
            player.getInventory().setLeggings(getLeggings());

        if (getBoots() != null)
            player.getInventory().setBoots(getBoots());
    }

    public String getGameSlug() {
        return gameSlug;
    }
}
