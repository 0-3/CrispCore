package network.reborn.core.Module.Hub.Listeners;

import network.reborn.core.API.DonorRank;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Events.PlayerRunJoinEvent;
import network.reborn.core.Module.Hub.Hub;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.*;

public class PlayerJoin implements Listener {
    private Hub hub;

    public PlayerJoin(Hub hub) {
        this.hub = hub;
    }

    @EventHandler
    public void onPlayerRunJoin(final PlayerRunJoinEvent event) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
//		Team team = scoreboard.registerNewTeam(event.getPlayer().getPlayer().getName());
//		team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
//		team.addPlayer(event.getPlayer().getPlayer());
        Objective objective = scoreboard.registerNewObjective("dummy", "dummy");
        objective.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "Reborn Network");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore("" + ChatColor.GREEN);
        score.setScore(5);

        score.setScore(4);
        if (event.getPlayer().getServerRank() != ServerRank.DEFAULT) {
            score = objective.getScore("" + ChatColor.WHITE + "Rank: " + event.getPlayer().getServerRank().getNiceName(true));
        } else {
            score = objective.getScore("" + ChatColor.WHITE + "Rank: " + event.getPlayer().getDonorRank().getNiceName(true));
        }
        score.setScore(3);

        score = objective.getScore("" + ChatColor.GREEN + ChatColor.GREEN);
        score.setScore(2);

        score = objective.getScore("" + ChatColor.WHITE + "Gold: " + ChatColor.GOLD + event.getPlayer().getBalance("Gold"));
        score.setScore(1);

        score = objective.getScore("" + ChatColor.GREEN + ChatColor.GREEN + ChatColor.GREEN);
        score.setScore(0);

        if (!event.getPlayer().isOnline())
            return;

        event.getPlayer().getPlayer().setScoreboard(scoreboard);

        // Allow admins and owner to fly

        // Set XP and Level
        event.getPlayer().getPlayer().setLevel(0);
        event.getPlayer().getPlayer().setExp(0);

        event.getPlayer().getPlayer().getInventory().clear();
        event.getPlayer().getPlayer().getInventory().setArmorContents(null);

        // Compass
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "Server Selector" + ChatColor.GRAY + " (Right Click)");
        compass.setItemMeta(compassMeta);
        event.getPlayer().getPlayer().getInventory().setItem(0, compass);

        // My Profile
        ItemStack profile = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
        profileMeta.setOwner(event.getPlayer().getPlayer().getName());
        profileMeta.setDisplayName(ChatColor.GREEN + "My Profile" + ChatColor.RED + " (Coming Soon)");
        profile.setItemMeta(profileMeta);
        event.getPlayer().getPlayer().getInventory().setItem(1, profile);

        // Admin system
        RebornPlayer rebornPlayer = event.getPlayer();
        if (rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
            ItemStack admin = new ItemStack(Material.WATCH);
            ItemMeta adminMeta = admin.getItemMeta();
            adminMeta.setDisplayName(ChatColor.GREEN + "Server Manager" + ChatColor.GRAY + " (Right Click)");
            admin.setItemMeta(adminMeta);
            rebornPlayer.getPlayer().getInventory().setItem(2, admin);

            ItemStack coveBox = new ItemStack(Material.ENDER_CHEST);
            ItemMeta itemMeta = coveBox.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "Fun Box" + ChatColor.GRAY + " (Right Click)");
            coveBox.setItemMeta(itemMeta);
            rebornPlayer.getPlayer().getInventory().setItem(4, coveBox);
        }

        // Players
        ItemStack hider = new ItemStack(Material.INK_SACK, 1, (short) 10);
        ItemMeta hiderMeta = hider.getItemMeta();
        hiderMeta.setDisplayName(ChatColor.WHITE + "Players: " + ChatColor.GREEN + "On");
        hider.setItemMeta(hiderMeta);
//		event.getPlayer().getPlayer().getInventory().setItem(8, hider);

        rebornPlayer.sendTabTitle(ChatColor.AQUA + "        Reborn Network          ", "reborn.network");

        if (rebornPlayer.canPlayer(DonorRank.VIP)) {
            for (Location loc : Hub.vipBlocks)
                rebornPlayer.getPlayer().sendBlockChange(loc, Material.AIR, (byte) 0);
        } else {
            for (Location loc : Hub.vipBlocks)
                rebornPlayer.getPlayer().sendBlockChange(loc, Material.BARRIER, (byte) 0);
        }

        hub.hidePlayerFromHidden(event.getPlayer().getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
        if (rebornPlayer.canPlayer(DonorRank.VIP)) {
            for (Location loc : Hub.vipBlocks)
                rebornPlayer.getPlayer().sendBlockChange(loc, Material.AIR, (byte) 0);
        } else {
            for (Location loc : Hub.vipBlocks)
                rebornPlayer.getPlayer().sendBlockChange(loc, Material.BARRIER, (byte) 0);
        }
    }

}
