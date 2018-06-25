package network.reborn.core.Module.Games.SkyWars;

public enum SkyWarsMode {
    DEFAULT, OP;

    public String getNiceName() {
        switch (this) {
            case DEFAULT:
                return "Default";
            case OP:
                return "OP";
        }
        return "";
    }
}

