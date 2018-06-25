package network.reborn.core.Module.Games.UltraHardcoreReddit.UBL;

import org.bukkit.Bukkit;

import java.util.*;

/**
 * Created by ethan on 5/29/2017.
 */
public class CheckLocalUBL {

    private static Map<String, BanEntry> banlistByIGN;
    private static Map<UUID, BanEntry> banlistByUUID;

    /**
     * Update the entire ban-list using raw CSV lines, overwriting any
     * previous settings
     *
     * @param banlist The new ban-list
     */
    public static void setBanList(String fieldNamesCSV, List<String> banlist) {
        String[] fieldNames = CSVReader.parseLine(fieldNamesCSV);
        if (!Arrays.asList(fieldNames).contains(getIGNFieldName())) {
            Bukkit.getLogger().warning("There is no matching IGN field (" + getIGNFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.ign' in your config.yml to the correct field name");
            Bukkit.broadcast("[AutoUBL] No IGN field found in the ban-list data. If you also have no UUID field then your server will be locked to non-ops for your protection. Please see your server logs for details in how to fix this issue", "bukkit.op");
        }
        if (!Arrays.asList(fieldNames).contains(getUUIDFieldName())) {
            Bukkit.getLogger().warning("There is no matching UUID field (" + getUUIDFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.uuid' in your config.yml to the correct field name");
            Bukkit.broadcast("[AutoUBL] No UUID field found in the ban-list data. If Mojang has not yet allowed name-changing, this is not a problem. Otherwise, please check your server logs for details on how to fix this issue", "bukkit.op");
        }
        banlistByIGN = new HashMap<>();
        banlistByUUID = new HashMap<>();
        for (String rawCSV : banlist) {
            BanEntry banEntry = new BanEntry(fieldNames, rawCSV);
            String ign = banEntry.getData(getIGNFieldName());
            if (ign != null) {
                banlistByIGN.put(ign.toLowerCase(), banEntry);
                banEntry.setIgn(ign);
            }
            String uuidString = banEntry.getData(getUUIDFieldName()).trim();
            if (uuidString != null) {
                if (uuidString.length() == 32) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(uuidString.substring(0, 8)).append('-');
                    sb.append(uuidString.substring(8, 12)).append('-');
                    sb.append(uuidString.substring(12, 16)).append('-');
                    sb.append(uuidString.substring(16, 20)).append('-');
                    sb.append(uuidString.substring(20, 32));
                    uuidString = sb.toString();
                }
                if (uuidString.length() == 36) {
                    UUID uuid = UUID.fromString(uuidString);
                    banlistByUUID.put(uuid, banEntry);
                    banEntry.setUUID(uuid);
                } else {
                    Bukkit.getLogger().warning("Invalid UUID in ban-list for " + ign + ": " + uuidString);
                }
            }
        }
    }

    /**
     * @return The field name to check for the player's in-game name
     */
    public static String getIGNFieldName() {
        return "IGN";
    }

    /**
     * @return The field name to check for the player's universally unique
     * identifier
     */
    public static String getUUIDFieldName() {
        return "UUID";
    }

    public static Boolean isBanned(UUID uuid) {
        return banlistByUUID.keySet().contains(uuid);
    }
}
