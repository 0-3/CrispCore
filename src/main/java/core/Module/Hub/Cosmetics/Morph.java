package network.reborn.core.Module.Hub.Cosmetics;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class Morph extends Cosmetic implements Listener {
    private DisguiseType disguiseType;
    private MobDisguise disguise;
    private Material skillMaterial;
    private ItemStack skillItem;
    private ArrayList<UUID> currentlyUsing = new ArrayList<>();

    public Morph(String name, String slug, Material material, DisguiseType disguiseType) {
        super(name, slug, CosmeticType.MORPH, material);
        this.disguiseType = disguiseType;
        disguise = new MobDisguise(disguiseType);
        RebornCore.getRebornCore().getServer().getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public Morph(String name, String slug, Material material, DisguiseType disguiseType, int cost) {
        super(name, slug, CosmeticType.MORPH, material, cost);
        this.disguiseType = disguiseType;
        disguise = new MobDisguise(disguiseType);
        RebornCore.getRebornCore().getServer().getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public DisguiseType getDisguiseType() {
        return disguiseType;
    }

    public MobDisguise getDisguise() {
        return disguise;
    }

    public Material getSkillMaterial() {
        return skillMaterial;
    }

    public void setSkillMaterial(Material skillMaterial) {
        this.skillMaterial = skillMaterial;
    }

    public ItemStack getSkillItem() {
        return skillItem;
    }

    public void setSkillItem(ItemStack skillItem) {
        this.skillItem = skillItem;
    }

    public void doMorph(Player player) {
        player.sendMessage("Doing morph");
        DisguiseAPI.disguiseToAll(player, getDisguise());
        currentlyUsing.add(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                player.sendMessage("Calling #removeMorph");
                removeMorph(player);
            }
        }, 100L);
    }

    public void removeMorph(Player player) {
        DisguiseAPI.undisguiseToAll(player);
        currentlyUsing.remove(player.getUniqueId());
    }

    public void doSkill(Player player) {
        // Override me :D
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().toString().contains("LEFT") || getSkillMaterial() == null || event.getItem() == null || event.getItem().getType() != getSkillMaterial())
            return;

        if (!currentlyUsing.contains(player.getUniqueId()))
            return;

        // TODO Cooldowns and stuffz
        doSkill(player);
    }

}
