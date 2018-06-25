package network.reborn.core.Module.Games;

import org.bukkit.GameMode;
import org.bukkit.Location;

public class GameSettings {
    private boolean build = false;
    private boolean destroy = false;
    private boolean pvp = true;
    private boolean pve = true;
    private boolean hunger = false;
    private boolean dropItems = false;
    private boolean pickupItems = false;
    private boolean buckets = false;
    private boolean stats = true;
    private boolean frozen = false;
    private boolean damage = true;
    private boolean dropItemsOnDeath = false;
    private boolean chatInLobby = true;
    private boolean chatInGame = true;
    private boolean deadBodies = false;
    private boolean forceRespawn = true;
    private boolean autoPopulateChests = false;
    private boolean includeKitSelector = true;
    private boolean giveKitsOnStart = true;
    private boolean disableWeather = true;
    private boolean isTeams = false;
    private boolean healthInTab = false;
    private int teamSize = 2;
    private Location gameLobby = null;
    private GameMode defaultGameMode = GameMode.ADVENTURE;

    public GameSettings() {
    }

    public boolean isTeams() {
        return isTeams;
    }

    public void setTeams(boolean teams) {
        this.isTeams = teams;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int size) {
        this.teamSize = size;
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isPve() {
        return pve;
    }

    public void setPve(boolean pve) {
        this.pve = pve;
    }

    public boolean showHealthInTab() {
        return healthInTab;
    }

    public void setHealthInTab(Boolean health) {
        this.healthInTab = health;
    }

    public boolean isHunger() {
        return hunger;
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public boolean isPickupItems() {
        return pickupItems;
    }

    public void setPickupItems(boolean pickupItems) {
        this.pickupItems = pickupItems;
    }

    public boolean isBuckets() {
        return buckets;
    }

    public void setBuckets(boolean buckets) {
        this.buckets = buckets;
    }

    public boolean isStats() {
        return stats;
    }

    public void setStats(boolean stats) {
        this.stats = stats;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isDamage() {
        return damage;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    public boolean isDropItemsOnDeath() {
        return dropItemsOnDeath;
    }

    public void setDropItemsOnDeath(boolean dropItemsOnDeath) {
        this.dropItemsOnDeath = dropItemsOnDeath;
    }

    public boolean isChatInLobby() {
        return chatInLobby;
    }

    public void setChatInLobby(boolean chatInLobby) {
        this.chatInLobby = chatInLobby;
    }

    public boolean isChatInGame() {
        return chatInGame;
    }

    public void setChatInGame(boolean chatInGame) {
        this.chatInGame = chatInGame;
    }

    public boolean isDeadBodies() {
        return deadBodies;
    }

    public void setDeadBodies(boolean deadBodies) {
        this.deadBodies = deadBodies;
    }

    public boolean isForceRespawn() {
        return forceRespawn;
    }

    public void setForceRespawn(boolean forceRespawn) {
        this.forceRespawn = forceRespawn;
    }

    public boolean isAutoPopulateChests() {
        return autoPopulateChests;
    }

    public void setAutoPopulateChests(boolean autoPopulateChests) {
        this.autoPopulateChests = autoPopulateChests;
    }

    public boolean isIncludeKitSelector() {
        return includeKitSelector;
    }

    public void setIncludeKitSelector(boolean includeKitSelector) {
        this.includeKitSelector = includeKitSelector;
    }

    public boolean isGiveKitsOnStart() {
        return giveKitsOnStart;
    }

    public void setGiveKitsOnStart(boolean giveKitsOnStart) {
        this.giveKitsOnStart = giveKitsOnStart;
    }

    public Location getGameLobby() {
        return gameLobby;
    }

    public void setGameLobby(Location gameLobby) {
        this.gameLobby = gameLobby;
    }

    public GameMode getDefaultGameMode() {
        return defaultGameMode;
    }

    public void setDefaultGameMode(GameMode defaultGameMode) {
        this.defaultGameMode = defaultGameMode;
    }

    public boolean isDisableWeather() {
        return disableWeather;
    }

    public void setDisableWeather(boolean disableWeather) {
        this.disableWeather = disableWeather;
    }

}
