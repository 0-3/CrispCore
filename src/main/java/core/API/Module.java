package network.reborn.core.API;

public enum Module {
    HUB, SKYWARS, TACTICALASSAULT, PORT_PROTECTOR, ULTRA_HARDCORE, UHC_REDDIT, SMP, APIONLY, OTHER;

    public String getNiceName() {
        switch (this) {
            default:
                return this.toString();
            case HUB:
                return "HUB";
            case SKYWARS:
                return "SkyWars";
            case PORT_PROTECTOR:
                return "Port Protector";
            case ULTRA_HARDCORE:
                return "Ultra Hardcore";
            case UHC_REDDIT:
                return "Reddit UHC";
            case SMP:
                return "SMP";
            case TACTICALASSAULT:
                return "Tactical Assault";
            case APIONLY:
                return "ApiOnly";
        }
    }
}
