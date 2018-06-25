package network.reborn.core.API;

import network.reborn.core.Util.OtherUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ServerSelectorItem {
    private String title;
    private Material material;
    private short data;
    private String desc;
    private ServerSelectorItemType serverSelectorItemType;
    private String serverNameORPrefix;

    public ServerSelectorItem(String title, Material material, short data, String desc, ServerSelectorItemType serverSelectorItemType) {
        this.title = title;
        this.material = material;
        this.data = data;
        this.desc = desc;
        this.serverSelectorItemType = serverSelectorItemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public ServerSelectorItemType getServerSelectorItemType() {
        return serverSelectorItemType;
    }

    public void setServerSelectorItemType(ServerSelectorItemType serverSelectorItemType) {
        this.serverSelectorItemType = serverSelectorItemType;
    }

    public String getServerNameORPrefix() {
        return serverNameORPrefix;
    }

    public void setServerNameORPrefix(String serverNameORPrefix) {
        this.serverNameORPrefix = serverNameORPrefix;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material, 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (getServerSelectorItemType() == ServerSelectorItemType.SECRET) {
            itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.MAGIC + getTitle());
        } else if (getServerSelectorItemType() == ServerSelectorItemType.SOON) {
            itemMeta.setDisplayName(ChatColor.RED + getTitle());
        } else {
            itemMeta.setDisplayName(ChatColor.GREEN + getTitle());
        }
        if (desc != null && !desc.equals("") && getServerSelectorItemType() != ServerSelectorItemType.SECRET)
            itemMeta.setLore(OtherUtil.stringToLore(desc, ChatColor.GRAY));
        if (getServerSelectorItemType() == ServerSelectorItemType.SOON || getServerSelectorItemType() == ServerSelectorItemType.SECRET) {
            List<String> lore = new ArrayList<>();
            if (itemMeta.hasLore())
                lore = itemMeta.getLore();

            if (lore.isEmpty())
                lore.add(" ");
            lore.add(ChatColor.RED + "Coming Soon");
            lore.add(" ");
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        return itemStack;
    }

}
