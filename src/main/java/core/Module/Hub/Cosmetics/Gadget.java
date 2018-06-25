package network.reborn.core.Module.Hub.Cosmetics;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Lag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Gadget extends Cosmetic implements Listener {
    public HashMap<String, Integer> cooldownMap = new HashMap<>(); // OVERRIDE THIS WHEN YOU EXTEND OR IT WILL NOT WORK
    private int cooldown = 0;

    public Gadget(String name, String slug, Material material) {
        super(name, slug, CosmeticType.GADGET, material);
        RebornCore.getRebornCore().getServer().getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public Gadget(String name, String slug, Material material, int cost) {
        super(name, slug, CosmeticType.GADGET, material, cost);
        RebornCore.getRebornCore().getServer().getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public void doGadget(final Player player) {
        // Override this
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown - 1;
    }

    public boolean hitCooldown(Player player) {
        return getCooldown() != 0 && cooldownMap.containsKey(player.getName());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            Player player = event.getPlayer();
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
            ItemStack itemStack = event.getItem();
            if (itemStack.getType() == getMaterial() && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(getName())) {
                event.setCancelled(true);
                if (Lag.getTPS() < 16) {
                    player.sendMessage(ChatColor.RED + "Gadgets are currently disabled on this server");
                } else if (hitCooldown(player) && !(player.isSneaking() && rebornPlayer.canPlayer(ServerRank.ADMIN))) {
                    player.sendMessage(ChatColor.YELLOW + "Please wait " + (cooldownMap.get(player.getName()) + 1) + " seconds before using this again...");
                } else {
                    doGadget(player);
                    if (getCooldown() > 0) cooldownMap.put(player.getName(), getCooldown());
                }
            }
            player.updateInventory();
        }
    }

}
