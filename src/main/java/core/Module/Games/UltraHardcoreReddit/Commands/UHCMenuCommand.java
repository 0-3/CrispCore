package network.reborn.core.Module.Games.UltraHardcoreReddit.Commands;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.UHCMenu;
import network.reborn.core.Util.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ethan on 2/7/2017.
 */
public class UHCMenuCommand extends AbstractCommand {
    public UHCMenuCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        UHCMenu.getUHCMainMenu((Player) sender);
        return true;
    }
}
