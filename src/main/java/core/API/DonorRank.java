package network.reborn.core.API;

import org.bukkit.ChatColor;

public enum DonorRank {
    DEFAULT, VIP, VIPPLUS, REBORN;

    public String getNiceName(boolean includeColor) {
        String returnStr = "";
        if (includeColor)
            returnStr += getChatColor();
        switch (this) {
            default:
            case DEFAULT:
                return returnStr + "Default";
            case VIP:
                return returnStr + "VIP";
            case VIPPLUS:
                return returnStr + "VIP+";
            case REBORN:
                return returnStr + "Reborn";
        }
    }

    public String getTabName() {
        switch (this) {
            default:
            case DEFAULT:
                return "";
            case VIP:
                return getChatColor() + "" + ChatColor.BOLD + "VIP" + " " + ChatColor.RESET;
            case VIPPLUS:
                return getChatColor() + "" + ChatColor.BOLD + "VIP+" + " " + ChatColor.RESET;
            case REBORN:
                return getChatColor() + "" + ChatColor.BOLD + "REBORN" + " " + ChatColor.RESET;
        }
    }

    public int getBlockColor() {
        switch (this) {
            default:
            case DEFAULT:
                return 7;
            case VIP:
                return 13;
            case VIPPLUS:
                return 10;
            case REBORN:
                return 1;
        }
    }

    public ChatColor getChatColor() {
        switch (this) {
            default:
            case DEFAULT:
                return ChatColor.GRAY;
            case VIP:
                return ChatColor.AQUA;
            case VIPPLUS:
                return ChatColor.DARK_AQUA;
            case REBORN:
                return ChatColor.GOLD;
        }
    }

    public int getRankID() {
        switch (this) {
            case DEFAULT:
                return 0;
            case VIP:
                return 10;
            case VIPPLUS:
                return 20;
            case REBORN:
                return 30;
        }
        return 0;
    }

}
