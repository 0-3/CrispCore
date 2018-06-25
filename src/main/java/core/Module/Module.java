package network.reborn.core.Module;

import network.reborn.core.API.CoveServer;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Lag;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Module implements Listener {
    protected String name;
    protected String slug;
    protected network.reborn.core.API.Module module;
    protected RebornCore rebornCore;
    protected CoveServer coveServer = null;
    protected Inventory serverManager;

    public Module(String name, String slug, RebornCore rebornCore, network.reborn.core.API.Module module) {
        this.name = name;
        this.slug = slug;
        this.rebornCore = rebornCore;
        this.module = module;
        onEnable();
        runTasks();
        setupServerManager();
        coveServer = new CoveServer(name, slug, Bukkit.getMaxPlayers(), true, module);
        Bukkit.getPluginManager().registerEvents(this, RebornCore.getRebornCore());
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void setupServerManager() {
        serverManager = Bukkit.createInventory(null, 45, "Server Manager");

        // TPS
        ItemStack tps = new ItemStack(Material.SLIME_BALL);
        ItemMeta tpsMeta = tps.getItemMeta();
        tpsMeta.setDisplayName(ChatColor.WHITE + "TPS: Loading...");
        tps.setItemMeta(tpsMeta);
        serverManager.setItem(12, tps);

        // RAM
        ItemStack ram = new ItemStack(Material.SLIME_BALL);
        ItemMeta ramMeta = ram.getItemMeta();
        ramMeta.setDisplayName(ChatColor.WHITE + "RAM: Loading...");
        ram.setItemMeta(ramMeta);
        serverManager.setItem(13, ram);

        // RAM
        ItemStack version = new ItemStack(Material.SLIME_BALL);
        ItemMeta versionMeta = version.getItemMeta();
        versionMeta.setDisplayName(ChatColor.WHITE + "Version: " + RebornCore.getRebornCore().getDescription().getVersion());
        version.setItemMeta(versionMeta);
        serverManager.setItem(22, version);

        // Uptime
        ItemStack uptime = new ItemStack(Material.SLIME_BALL);
        ItemMeta uptimeMeta = uptime.getItemMeta();
        uptimeMeta.setDisplayName(ChatColor.WHITE + "Uptime: 0 Seconds");
        uptime.setItemMeta(uptimeMeta);
        serverManager.setItem(14, uptime);

        // Restart Server
        ItemStack restart = new ItemStack(Material.BARRIER);
        ItemMeta restartMeta = restart.getItemMeta();
        restartMeta.setDisplayName(ChatColor.WHITE + "Restart this Server");
        restart.setItemMeta(restartMeta);
        serverManager.setItem(29, restart);

        // Whitelist Server
        ItemStack whitelist = new ItemStack(Material.BARRIER);
        ItemMeta whitelistMeta = whitelist.getItemMeta();
        whitelistMeta.setDisplayName(ChatColor.WHITE + "Whitelist this Server");
        whitelist.setItemMeta(whitelistMeta);
        serverManager.setItem(31, whitelist);

        // Change Server Module
        ItemStack module = new ItemStack(Material.BARRIER);
        ItemMeta moduleMeta = module.getItemMeta();
        moduleMeta.setDisplayName(ChatColor.WHITE + "Change Server Module");
        String currentModuleName = getName();
        moduleMeta.setLore(OtherUtil.stringToLore("Current Module: " + currentModuleName, ChatColor.GRAY));
        module.setItemMeta(moduleMeta);
        serverManager.setItem(33, module);

        Bukkit.getScheduler().runTaskTimerAsynchronously(RebornCore.getRebornCore(), new Runnable() {
            int i = 0;

            @Override
            public void run() {
                ItemStack tps = new ItemStack(Material.SLIME_BALL);
                ItemMeta tpsMeta = tps.getItemMeta();
                double tpsCount = Math.round(Lag.getTPS() * 100.0) / 100.0;
                tpsMeta.setDisplayName(ChatColor.WHITE + "TPS: " + tpsCount);
                tps.setItemMeta(tpsMeta);
                tps.setAmount((int) tpsCount);
                serverManager.setItem(12, tps);

                if (tpsCount > 0 && tpsCount < 10) {
//                    RebornCore.getRebornAPI().messageAllStaff(ServerRank.ADMIN, true, ChatColor.RED + "WARNING: TPS is currently at " + tpsCount, ChatColor.RED + "Use /sm to view the server manager");
                } else if (tpsCount > 0 && tpsCount < 15) {
//                    RebornCore.getRebornAPI().messageAllStaff(ServerRank.ADMIN, true, ChatColor.GOLD + "WARNING: TPS is currently at " + tpsCount, ChatColor.GOLD + "Use /sm to view the server manager");
                }

                ItemStack ram = new ItemStack(Material.SLIME_BALL);
                ItemMeta ramMeta = ram.getItemMeta();
                ramMeta.setDisplayName(ChatColor.WHITE + "RAM: " + Lag.getUsedRAM() + "/" + Lag.getMaxRam() + " MB");
                ram.setItemMeta(ramMeta);
                serverManager.setItem(13, ram);

                ItemStack uptime = new ItemStack(Material.SLIME_BALL);
                ItemMeta uptimeMeta = uptime.getItemMeta();
                if (i > 300) {
                    uptimeMeta.setDisplayName(ChatColor.WHITE + "Uptime: " + (i / 60) + " Minutes");
                } else {
                    uptimeMeta.setDisplayName(ChatColor.WHITE + "Uptime: " + i + " Seconds");
                }
                List<String> lore = new ArrayList<>();
                if (i >= 3600) {
                    lore.add(((i / 60) / 60) + " Hour(s)");
                    lore.add((i - (((i / 60) / 60)) * 60) + " Minute(s)");
//                    lore.add((i - ( (i / 60) * 60 ) ) + " Second(s)");
                } else if (i >= 60) {
                    lore.add((i / 60) + " Minute(s)");
                    lore.add((i - ((i / 60) * 60)) + " Second(s)");
                }
                uptimeMeta.setLore(lore);
                uptime.setItemMeta(uptimeMeta);
                serverManager.setItem(14, uptime);
                i++;
            }
        }, 20L, 20L);
    }

    public Inventory getServerManager() {
        return serverManager;
    }

    public void runTasks() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(RebornCore.getRebornCore(), () -> {
            if (Lag.getMaxRam() == null)
                return;
            RebornCore.getCoveAPI().getModule().getCoveServer().updateTotalRAM(Lag.getMaxRam(), false);
        }, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(RebornCore.getRebornCore(), () -> {
            if (Lag.getMaxRam() == null || Lag.getUsedRAM() == null || RebornCore.getCoveAPI().getModule() == null || RebornCore.getCoveAPI().getModule().getCoveServer() == null)
                return;
            RebornCore.getCoveAPI().getModule().getCoveServer().updateUsedRAM(Lag.getUsedRAM(), false);
            RebornCore.getCoveAPI().getModule().getCoveServer().updateFreeRAM(Lag.getFreeRAM(), false);
            RebornCore.getCoveAPI().getModule().getCoveServer().updateTPS(Lag.getTPS(), false);
        }, 20L, 20L);
    }

    public CoveServer getCoveServer() {
        return coveServer;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public network.reborn.core.API.Module getModule() {
        return module;
    }

}
