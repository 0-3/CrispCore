package network.reborn.core.API;

import net.milkbowl.vault.permission.Permission;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by ethan on 12/28/2016.
 */
public class Permission_RebornPermissions extends Permission {
    private final String name = "RebornCore";
    private RebornCore rebornCore;

    public Permission_RebornPermissions(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("RebornCore").isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        try {
            return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(playerName).getUniqueId()).hasPermission(permission);
        } catch (Exception e) {
            return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId()).hasPermission(permission);
        }
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupHas(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupAdd(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupRemove(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerInGroup(String s, String playerName, String groupName) {
        Boolean b = false;
        ServerRank r = ServerRank.valueOf(groupName);
        DonorRank d = DonorRank.valueOf(groupName);
        if (r != null && r instanceof ServerRank) {
            b = true;
        }
        try {
            if (b) {
                return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(playerName).getUniqueId()).isPlayer(r);
            } else {
                return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(playerName).getUniqueId()).isPlayer(d);
            }
        } catch (Exception e) {
            if (b) {
                return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(playerName).getUniqueId()).isPlayer(r);
            } else {
                return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getPlayer(playerName).getUniqueId()).isPlayer(d);
            }
        }
    }

    @Override
    public boolean playerAddGroup(String s, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String s, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String s, String player) {
        String server = RebornCore.getCoveAPI().getCovePlayer(Bukkit.getOfflinePlayer(player).getUniqueId()).getServerRank().getNiceName(false);
        String donor = "";
        try {
            donor = RebornCore.getCoveAPI().getCovePlayer(Bukkit.getOfflinePlayer(player).getUniqueId()).getDonorRank().getNiceName(false);
        } catch (Exception e) {

        }
        return new String[]{server, donor};

    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return RebornCore.getCoveAPI().getCovePlayer(Bukkit.getOfflinePlayer(player).getUniqueId()).getServerRank().getNiceName(false);
    }

    @Override
    public String[] getGroups() {
        ArrayList<String> s = new ArrayList<>();
        for (ServerRank r : ServerRank.values()) {
            s.add(r.name());
        }
        return s.toArray(new String[s.size()]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
