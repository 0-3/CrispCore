package network.reborn.core.Module.SMP.Handlers;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ShopItem {
    private Shop.ShopItemType shopItemType = null;
    private ItemStack itemStack = null;
    private int price = 100;

    public ShopItem(Shop.ShopItemType shopItemType, ItemStack itemStack, int price) {
        this.shopItemType = shopItemType;
        this.itemStack = itemStack;
        this.price = price;
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
            itemMeta.setLore(OtherUtil.stringToLore("Price: " + ChatColor.GREEN + getPrice(), ChatColor.GRAY));
            itemStack.setItemMeta(itemMeta);
            return itemStack;

        }
        return itemStack;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean canPlayerAfford(Player player) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        return canPlayerAfford(rebornPlayer);
    }

    public boolean canPlayerAfford(RebornPlayer rebornPlayer) {
        return rebornPlayer.getBalance("SMP") >= getPrice();
    }

    public boolean doPurchase(Player player) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        if (!canPlayerAfford(rebornPlayer))
            return false;

        rebornPlayer.takeBalance("SMP", getPrice());
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
