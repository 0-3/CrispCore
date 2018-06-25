package network.reborn.core.Module.Hub.Cosmetics;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Crate {
    private ArrayList<Cosmetic> winnings = new ArrayList<>();

    public Crate(ArrayList<Cosmetic> winnings) {
        this.winnings = winnings;
    }

    public ArrayList<Cosmetic> getWinnings() {
        return winnings;
    }

    public Cosmetic getRandomWinning() {
        return getWinnings().get(OtherUtil.randInt(0, getWinnings().size() - 1));
    }

    public void awardWinning(Player player) {
        Cosmetic winning = getRandomWinning();
        if (winning == null)
            return;

        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        if (winning.playerHas(player)) {
            // TODO Decide what this will do, for now it gives 33% of the value from the item cost
            int amount = winning.getCost() / 3;
            rebornPlayer.giveBalance("Gold", amount);
            player.sendMessage(ChatColor.GREEN + "You won the " + ChatColor.YELLOW + winning.getName() + ChatColor.GREEN + " " + winning.getCosmeticType().toString() + ", You've been given " + ChatColor.GOLD + amount + ChatColor.GREEN + " coins cause you already had this");
            return;
        }

        rebornPlayer.givePermission(winning.getPermission());
        player.sendMessage(ChatColor.GREEN + "You won the " + ChatColor.YELLOW + winning.getName() + ChatColor.GREEN + " " + winning.getCosmeticType().toString());
    }

}
