package network.reborn.core.Module.Games.UltraHardcoreReddit.Commands;

import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.ScenariosMenu;
import network.reborn.core.Util.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ethan on 12/18/2016.
 */
public class ScenarioTestCommand extends AbstractCommand {
    public ScenarioTestCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.getName().equals("ElectronicWizard") || sender.getName().equals("Conorz")
                    || sender.getName().equals("0_3")) {
                if (args.length == 0) {
                    ScenariosMenu.getDisplayGUI((Player) sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("manager")) {
                    ScenariosMenu.getManagerGUI((Player) sender);
                }
            }
        }

        return true;
    }
}
