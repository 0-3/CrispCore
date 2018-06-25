package network.reborn.core.Module.Games.TacticalAssault;

import network.reborn.core.Util.OtherUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ShopItem {
    private Shop.ShopItemType shopItemType = null;
    private ItemStack itemStack = null;
    private ItemStack price = new ItemStack(Material.AIR);
    private int priceAmount = 0;

    public ShopItem(Shop.ShopItemType shopItemType, ItemStack itemStack, ItemStack price, int amount) {
        this.shopItemType = shopItemType;
        this.itemStack = itemStack;
        this.price = price;
        this.priceAmount = amount;
    }

    public Shop.ShopItemType getShopItemType() {
        return shopItemType;
    }

    public void setShopItemType(Shop.ShopItemType shopItemType) {
        this.shopItemType = shopItemType;
    }

    public ItemStack getItemStack() {
        ItemStack tmp = itemStack.clone();
        ItemMeta tmpMeta = tmp.getItemMeta();
        tmpMeta.setLore(null);
        tmp.setItemMeta(tmpMeta);
        return tmp;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack(boolean includeInfoLore) {
        if (includeInfoLore) {
            ItemStack itemStack = getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(OtherUtil.stringToLore("Price: " + getPriceAmount() + "x " + price.getType().name().replace("_", " ").toLowerCase(), ChatColor.GRAY));
            itemStack.setItemMeta(itemMeta);
            return itemStack;

        }
        return itemStack;
    }

    public ItemStack getPrice() {
        return price;
    }

    public void setPrice(ItemStack price) {
        this.price = price;
    }

    public int getPriceAmount() {
        return priceAmount;
    }

    public boolean canPlayerAfford(Player player) {
        return player.getInventory().containsAtLeast(price, priceAmount);
    }

    public boolean doPurchase(Player player) {
        if (!canPlayerAfford(player))
            return false;

        // WADAFUQ
        int i = 0;
        while (i < priceAmount) {
            player.getInventory().removeItem(price);
            i++;
        }
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(getItemStack());
        if (!left.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your inventory was full and some items were dropped on the floor!");
            for (Map.Entry<Integer, ItemStack> item : left.entrySet()) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item.getValue());
            }
        }
        return true;
    }
}
