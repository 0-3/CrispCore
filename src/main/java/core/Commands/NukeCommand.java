package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import java.util.ArrayList;
import java.util.Collection;

public class NukeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        }
        Collection<Player> targets = new ArrayList<>();
        if (args.length > 0) {
            targets = new ArrayList<>();
            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                if (player != null)
                    targets.add(player);
            }
        } else {
            targets.addAll(Bukkit.getOnlinePlayers());
        }

        for (Player player : targets) {
            if (player == null || !player.isOnline()) {
                continue;
            }
            Location loc = player.getLocation();
            World world = loc.getWorld();
            for (int x = -10; x <= 10; x += 5) {
                for (int z = -10; z <= 10; z += 5) {
                    final Location tntloc = new Location(world, loc.getBlockX() + x, world.getHighestBlockYAt(loc) + 64, loc.getBlockZ() + z);
                    final TNTPrimed tnt = world.spawn(tntloc, TNTPrimed.class);
                }
            }
        }
        return true;
    }

}
