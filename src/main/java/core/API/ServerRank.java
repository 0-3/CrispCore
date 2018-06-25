package network.reborn.core.API;

import org.bukkit.ChatColor;

public enum ServerRank {
    DEFAULT, MEDIA, HELPER, MODERATOR, SENIOR, DEVELOPER, ADMIN, OWNER;

    public String getNiceName(boolean includeColor) {
        String returnStr = "";
        if (includeColor)
            returnStr += getChatColor();
        switch (this) {
            default:
            case DEFAULT:
                return returnStr + "Default";
            case MEDIA:
                return returnStr + "Media";
            case HELPER:
                return returnStr + "Helper";
            case MODERATOR:
                return returnStr + "Moderator";
            case SENIOR:
                return returnStr + "Senior";
            case DEVELOPER:
                return returnStr + "Developer";
            case ADMIN:
                return returnStr + "Admin";
            case OWNER:
                return returnStr + "Owner";
        }
    }

    public String getTabName() {
        switch (this) {
            default:
            case DEFAULT:
                return ChatColor.GRAY + "";
            case MEDIA:
                return getChatColor() + "" + ChatColor.BOLD + "MEDIA" + " " + ChatColor.RESET;
            case HELPER:
                return getChatColor() + "" + ChatColor.BOLD + "HELPER" + " " + ChatColor.RESET;
            case MODERATOR:
                return getChatColor() + "" + ChatColor.BOLD + "MOD" + " " + ChatColor.RESET;
            case SENIOR:
                return getChatColor() + "" + ChatColor.BOLD + "SENIOR" + " " + ChatColor.RESET;
            case DEVELOPER:
                return getChatColor() + "" + ChatColor.BOLD + "DEV" + " " + ChatColor.RESET;
            case ADMIN:
                return getChatColor() + "" + ChatColor.BOLD + "ADMIN" + " " + ChatColor.RESET;
            case OWNER:
                return getChatColor() + "" + ChatColor.BOLD + "OWNER" + " " + ChatColor.RESET;
        }
    }

    public ChatColor getChatColor() {
        switch (this) {
            default:
            case DEFAULT:
                return ChatColor.GRAY;
            case MEDIA:
                return ChatColor.LIGHT_PURPLE;
            case HELPER:
                return ChatColor.GREEN;
            case MODERATOR:
                return ChatColor.DARK_GREEN;
            case SENIOR:
                return ChatColor.DARK_PURPLE;
            case DEVELOPER:
                return ChatColor.RED;
            case ADMIN:
                return ChatColor.RED;
            case OWNER:
                return ChatColor.DARK_RED;
        }
    }

    public int getBlockColor() {
        switch (this) {
            default:
            case DEFAULT:
                return 7;
            case MEDIA:
                return 1;
            case HELPER:
                return 11;
            case MODERATOR:
                return 13;
            case SENIOR:
                return 13;
            case DEVELOPER:
                return 14;
            case ADMIN:
                return 14;
            case OWNER:
                return 3;
        }
    }

    public int getRankID() {
        switch (this) {
            case DEFAULT:
                return 0;
            case MEDIA:
                return 5;
            case HELPER:
                return 20;
            case MODERATOR:
                return 50;
            case SENIOR:
                return 60;
            case DEVELOPER:
                return 90;
            case ADMIN:
                return 80;
            case OWNER:
                return 100;
        }
        return 0;
    }

}
