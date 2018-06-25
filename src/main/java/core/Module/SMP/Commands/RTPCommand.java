package network.reborn.core.Module.SMP.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.SMP.SMP;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class RTPCommand implements CommandExecutor {
    private HashMap<UUID, Long> lastRTP = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
        Player mcPlayer = (Player) sender;

        int cooldown = 300;

        switch (rebornPlayer.getDonorRank()) {
            case VIP:
                cooldown = 200;
                break;
            case VIPPLUS:
                cooldown = 120;
                break;
            case REBORN:
                cooldown = 60;
                break;
        }

        if (lastRTP.containsKey(mcPlayer.getUniqueId())) {
            Long lastTP = lastRTP.get(mcPlayer.getUniqueId());
            if ((System.currentTimeMillis() / 1000) - lastTP > cooldown) {
                lastRTP.remove(mcPlayer.getUniqueId());
            } else {
                mcPlayer.sendMessage(ChatColor.RED + "Please wait another " + (cooldown - ((System.currentTimeMillis() / 1000) - lastTP)) + " seconds before using /" + string + " again");
                return true;
            }
        }

        if (SMP.teleportList.contains(mcPlayer.getUniqueId())) {
            mcPlayer.sendMessage(ChatColor.RED + "You are already waiting to teleport");
            return true;
        }
        if (!rebornPlayer.canPlayer(ServerRank.ADMIN)) {
            mcPlayer.sendMessage(ChatColor.GOLD + "Teleporting in 3 seconds. Don't move!");
            SMP.teleportList.add(mcPlayer.getUniqueId());
        }
        // TODO Improve the teleport delay as if you do it quick enough you can teleport in less than 3 seconds...
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), () -> {
            if (!SMP.teleportList.contains(mcPlayer.getUniqueId()) && !rebornPlayer.canPlayer(ServerRank.ADMIN))
                return;
            SMP.teleportList.remove(mcPlayer.getUniqueId());
            if (!mcPlayer.isOnline())
                return;
            lastRTP.put(mcPlayer.getUniqueId(), System.currentTimeMillis() / 1000);
            mcPlayer.sendMessage(ChatColor.GOLD + "Teleporting...");
            String commandSpread = "spreadplayers 0 0 1 5000 false";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandSpread + " " + sender.getName());
        }, rebornPlayer.canPlayer(ServerRank.ADMIN) ? 1L : 20L * 3);

        return true;
    }
}