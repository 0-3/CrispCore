package network.reborn.core.Commands;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class FireballCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, final Command command, String string, String[] args) {
        if (sender instanceof Player) {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(((Player) sender).getUniqueId());
            if (!rebornPlayer.canPlayer(ServerRank.DEVELOPER)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do that");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command must be ran as a player");
            return true;
        }
        Player player = (Player) sender;

        Class<? extends Entity> type = Fireball.class;
        Projectile projectile;
        int speed = 2;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("small")) {
                type = SmallFireball.class;
            } else if (args[0].equalsIgnoreCase("arrow")) {
                type = Arrow.class;
            } else if (args[0].equalsIgnoreCase("skull")) {
                type = WitherSkull.class;
            } else if (args[0].equalsIgnoreCase("egg")) {
                type = Egg.class;
            } else if (args[0].equalsIgnoreCase("snowball")) {
                type = Snowball.class;
            } else if (args[0].equalsIgnoreCase("expbottle")) {
                type = ThrownExpBottle.class;
            } else if (args[0].equalsIgnoreCase("large")) {
                type = LargeFireball.class;
            }
        }
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        projectile = (Projectile) player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), type);
        projectile.setShooter(player);
        projectile.setVelocity(direction);
        return true;
    }

}
