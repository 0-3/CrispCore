package network.reborn.core.Module.Hub.Cosmetics;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Cosmetic {
    private String name;
    private String slug;
    private String desc = null;
    private CosmeticType cosmeticType;
    private Material material;
    private short data = 0;
    private int cost = 0;
    private boolean purchasable = true;
    private boolean privateBeta = false;

    public Cosmetic(String name, String slug, CosmeticType cosmeticType, Material material) {
        this.name = name;
        this.slug = slug;
        this.cosmeticType = cosmeticType;
        this.material = material;
    }

    public Cosmetic(String name, String slug, CosmeticType cosmeticType, Material material, int cost) {
        this.name = name;
        this.slug = slug;
        this.cosmeticType = cosmeticType;
        this.material = material;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public int getCost() {
        return cost;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public boolean isPrivateBeta() {
        return privateBeta;
    }

    public void setPrivateBeta(boolean privateBeta) {
        this.privateBeta = privateBeta;
    }

    public String getPermission() {
        return "cosmetic." + getCosmeticType().toLowerString() + "." + getSlug();
    }

    public boolean playerHas(Player player) {
        if (getCost() <= 0 && isPurchasable())
            return true;
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        return rebornPlayer.hasPermission(getPermission());
    }

    public boolean playerCanAfford(Player player) {
        if (getCost() <= 0)
            return true;

        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        return rebornPlayer.getBalance("Gold") >= getCost();
    }

    public boolean playerBuy(Player player) {
        if (!playerCanAfford(player))
            return false;
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        if (getCost() <= 0) {
            rebornPlayer.givePermission(getPermission());
            return true;
        }
        rebornPlayer.takeBalance("Gold", getCost());
        rebornPlayer.givePermission(getPermission());
        return true;
    }

}
