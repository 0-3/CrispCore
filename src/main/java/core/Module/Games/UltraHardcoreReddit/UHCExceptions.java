package network.reborn.core.Module.Games.UltraHardcoreReddit;

/**
 * Created by ethan on 12/16/2016.
 */
public class UHCExceptions {

    public static class ScenarioDoesntExistException extends Exception {
        public ScenarioDoesntExistException(String scenarioName) {
            super("The scenario \"" + scenarioName + "\" doesn't exist!");
        }
    }

    public static class ScenarioNotEnabledException extends Exception {
        public ScenarioNotEnabledException(String scenarioName) {
            super("\"" + scenarioName + "\" doesn't appear to be enabled!");
        }
    }

    public class ClassDoesntExistException extends Exception {
        public ClassDoesntExistException(String clazz) {
            super("The class \"" + clazz + "\" doesn't exist within the scope!");
        }
    }

}
