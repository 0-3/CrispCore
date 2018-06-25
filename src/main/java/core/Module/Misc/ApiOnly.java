package network.reborn.core.Module.Misc;

import network.reborn.core.Module.Module;
import network.reborn.core.RebornCore;

public class ApiOnly extends Module {
    RebornCore core = null;

    public ApiOnly(RebornCore rebornCore, network.reborn.core.API.Module module) {
        super("ApiOnly", "apionly", rebornCore, network.reborn.core.API.Module.APIONLY);
        core = rebornCore;
    }

    @Override
    public void onEnable() {
        core.getLogger().warning("This server has been started in API ONLY MODE." +
                "This means RebornAPI will simply function as a API for the server to pass basic data to and from the network. " +
                "It will not load any additional modules or features other than the basic permissions, commands," +
                " and chat functions necessary for the server to even successfully connect to the Proxy server.");
    }

    @Override
    public void onDisable() {
        core.getLogger().warning("API is now shutting down. RebornAPI onDisable functions will now occur for proper hand-off to the Minecraft Server shutdown.");
    }
}
