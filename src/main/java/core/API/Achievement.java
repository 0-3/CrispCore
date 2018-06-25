package network.reborn.core.API;

import org.bukkit.ChatColor;

public class Achievement {
    private String label;
    private String name;
    private String description = "";
    private Integer gold;

    public Achievement(String label, String name, Integer gold) {
        this.label = label;
        this.name = name;
        this.gold = gold;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean alreadyEarned(RebornPlayer rebornPlayer) {
        return rebornPlayer.hasPermission("achievement.earned." + name);
    }

    public void giveAchievement(RebornPlayer rebornPlayer) {
        if (alreadyEarned(rebornPlayer))
            return;
        rebornPlayer.giveBalance("Gold", gold, false, false);
        rebornPlayer.givePermission("achievement.earned." + name);
//		rebornPlayer.sendCentredMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Achievement Earned - " + ChatColor.GOLD + ChatColor.BOLD + getLabel());
        rebornPlayer.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "-------------------------------------------------");
        rebornPlayer.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Achievement Earned - " + ChatColor.GOLD + ChatColor.BOLD + getLabel());
        if (!getDescription().isEmpty())
            rebornPlayer.getPlayer().sendMessage(ChatColor.WHITE + getDescription());
        rebornPlayer.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "-------------------------------------------------");
    }

}
