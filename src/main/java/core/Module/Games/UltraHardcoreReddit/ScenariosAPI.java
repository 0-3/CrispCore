package network.reborn.core.Module.Games.UltraHardcoreReddit;

import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ethan on 12/16/2016.
 */
public class ScenariosAPI {
    public static HashMap<String, Class<?>> scenarios = new HashMap<String, Class<?>>();
    public static HashMap<String, Object> enabledScenariosInstance = new HashMap<String, Object>();

    public static void generateAllInformation() {
        Reflections r = new Reflections("network.reborn.core.Module.Games.UltraHardcoreReddit.Scenarios");
        Set<Class<?>> classes = r.getTypesAnnotatedWith(Scenario.class);
        for (Class<?> c : classes) {
            Bukkit.getLogger().info("SCENARIO: \"" + c.getAnnotation(Scenario.class).name() + "\"");
            scenarios.put(c.getAnnotation(Scenario.class).name(), c);
        }

    }

    public static void enableScenario(String name, String executor) throws UHCExceptions.ScenarioDoesntExistException {
        name = ChatColor.stripColor(name);
        if (enabledScenariosInstance.containsKey(name)) {
            return;
        }
        //Bukkit.broadcastMessage(scenarios.keySet().toString());
        if (scenarios.containsKey(name)) {
            //Bukkit.broadcastMessage("INCOMING NAME: \"" + name + "\"");
            Class<?> scenario = scenarios.get(name);
            try {
                Object es = scenario.newInstance();
                scenario.getMethod(scenario.getAnnotation(Scenario.class).enableMethod()).invoke(es);
                enabledScenariosInstance.put(name, es);
                //Register the listener.1
                Bukkit.getPluginManager().registerEvents((Listener) es, RebornCore.getRebornCore());
                UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + name + ChatColor.YELLOW + " has been enabled by " + ChatColor.AQUA + executor);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            throw new UHCExceptions.ScenarioDoesntExistException(name);
        }
    }

    public static void disableScenario(String name, String executor) throws UHCExceptions.ScenarioDoesntExistException, UHCExceptions.ScenarioNotEnabledException {
        name = ChatColor.stripColor(name);
        if (scenarios.containsKey(name)) {
            Class<?> scenario = scenarios.get(name);
            if (enabledScenariosInstance.containsKey(name)) {
                try {
                    Object es = enabledScenariosInstance.get(name);
                    scenario.getMethod(scenario.getAnnotation(Scenario.class).disableMethod()).invoke(es);
                    enabledScenariosInstance.remove(name, es);
                    //Unregister the Listener (hopefully!)
                    HandlerList.unregisterAll((Listener) es);
                    UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + name + ChatColor.YELLOW + " has been disabled by " + ChatColor.AQUA + executor);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                throw new UHCExceptions.ScenarioNotEnabledException(name);
            }
        } else {
            throw new UHCExceptions.ScenarioDoesntExistException(name);
        }


    }

}
