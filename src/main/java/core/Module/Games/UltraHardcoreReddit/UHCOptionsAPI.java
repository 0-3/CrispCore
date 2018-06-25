package network.reborn.core.Module.Games.UltraHardcoreReddit;

import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ethan on 1/13/2016.
 */
public class UHCOptionsAPI {
    public static HashMap<String, Class<?>> options = new HashMap<String, Class<?>>();
    //All options have to be "enabled". Some use being disabled as their configuration.
    public static HashMap<String, Object> optionsInstance = new HashMap<String, Object>();

    public static void generateAllInformation() {
        Reflections r = new Reflections("network.reborn.core.Module.Games.UltraHardcoreReddit.Options");
        Set<Class<?>> classes = r.getTypesAnnotatedWith(UHCOption.class);
        for (Class<?> c : classes) {
            Bukkit.getLogger().info("UHCOPTION: \"" + c.getAnnotation(UHCOption.class).name() + "\"");
            options.put(c.getAnnotation(UHCOption.class).name(), c);
            //We're going to test if this is a Option that should be preemptively enabled
            try {
                //Create object instance
                Object test = c.newInstance();
                //Check if enabled
                Boolean enabled = (Boolean) c.getMethod("isEnabled").invoke(test);
                if (enabled) {
                    //Invoke onEnable() method incase any commands or other data needs to be handled there
                    c.getMethod(c.getAnnotation(UHCOption.class).enableMethod()).invoke(test);
                }
                //Store new Object instance in HashMap
                optionsInstance.put(c.getAnnotation(UHCOption.class).name(), test);
                //Register class events
                Bukkit.getPluginManager().registerEvents((Listener) test, RebornCore.getRebornCore());
                //And ALLLLL the errors
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    public static void enableOption(String name, String executor) throws UHCExceptions.ScenarioDoesntExistException, UHCExceptions.ScenarioNotEnabledException {
        name = ChatColor.stripColor(name);
        if (options.containsKey(name)) {
            Class<?> scenario = options.get(name);
            try {
                Object es = optionsInstance.get(name);
                scenario.getMethod(scenario.getAnnotation(UHCOption.class).enableMethod()).invoke(es);
                UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + name + ChatColor.YELLOW + " has been enabled by " + ChatColor.AQUA + executor);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            throw new UHCExceptions.ScenarioDoesntExistException(name);
        }
    }


    public static void disableOption(String name, String executor) throws UHCExceptions.ScenarioDoesntExistException, UHCExceptions.ScenarioNotEnabledException {
        name = ChatColor.stripColor(name);
        if (options.containsKey(name)) {
            Class<?> scenario = options.get(name);
            if (optionsInstance.containsKey(name)) {
                try {
                    Object es = optionsInstance.get(name);
                    scenario.getMethod(scenario.getAnnotation(UHCOption.class).disableMethod()).invoke(es);
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

    public static Boolean isOptionEnabled(String name) {
        name = ChatColor.stripColor(name);
        if (options.containsKey(name)) {
            Class<?> scenario = options.get(name);
            try {
                Object es = optionsInstance.get(name);
                return (Boolean) scenario.getMethod("isEnabled").invoke(es);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
