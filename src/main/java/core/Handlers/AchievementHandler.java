package network.reborn.core.Handlers;

import network.reborn.core.API.Achievement;

import java.util.HashMap;

public class AchievementHandler {
    private HashMap<String, Achievement> achievements = new HashMap<>();

    public AchievementHandler() {
        setupAchievements();
    }

    private void setupAchievements() {
        Achievement welcome = new Achievement("Welcome to Reborn Network", "welcome", 100);
        welcome.setDescription("Earned once you've joined Reborn Network for the first time.");
        registerAchievement(welcome);

        Achievement killOwner = new Achievement("Kill the Owner", "kill-owner", 1000);
        killOwner.setDescription("Earned by killing the owner of Reborn Network.");
        registerAchievement(killOwner);

        Achievement killDeveloper = new Achievement("Kill a Developer", "kill-developer", 500);
        killDeveloper.setDescription("Earned by killing one of the developers of Cove Network.");
        registerAchievement(killDeveloper);
    }

    public void registerAchievement(Achievement achievement) {
        achievements.put(achievement.getName(), achievement);
    }

    public Achievement getAchievement(String name) {
        return achievements.get(name);
    }

}
