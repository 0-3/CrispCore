package network.reborn.core.Module.Hub.Cosmetics;

public enum CosmeticType {
    GADGET, MORPH, HAT, TRAIL, ARROW_TRAIL, COMPANION, EMOTE;

    @Override
    public String toString() {
        return super.toString();
    }

    public String toLowerString() {
        return toString().toLowerCase();
    }
}
