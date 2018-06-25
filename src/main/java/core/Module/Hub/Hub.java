package network.reborn.core.Module.Hub;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import network.reborn.core.API.*;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Events.RealPlayerMoveEvent;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.SkyWars.SkyWarsKitManager;
import network.reborn.core.Module.Hub.Cosmetics.Cosmetics;
import network.reborn.core.Module.Hub.Cosmetics.Gadgets.*;
import network.reborn.core.Module.Hub.Cosmetics.Hat;
import network.reborn.core.Module.Hub.Cosmetics.Morphs.Skeleton;
import network.reborn.core.Module.Hub.GUIs.SkyWarsShop;
import network.reborn.core.Module.Hub.Listeners.PlayerInteract;
import network.reborn.core.Module.Hub.Listeners.PlayerJoin;
import network.reborn.core.Module.Hub.Listeners.PlayerMove;
import network.reborn.core.Module.Hub.Listeners.WorldListeners;
import network.reborn.core.Module.Module;
import network.reborn.core.RebornCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Hub extends Module {
    public static HashMap<network.reborn.core.API.Module, Location> spawns = new HashMap<>();
    public static HashMap<UUID, network.reborn.core.API.Module> lobbies = new HashMap<>();
    public static ArrayList<UUID> hidden = new ArrayList<>();
    public static ServerSelector serverSelector;
    public static HashMap<Location, network.reborn.core.API.Module> signs = new HashMap<>();
    public static ArrayList<Location> beacons = new ArrayList<>();
    public static ArrayList<Location> vipBlocks = new ArrayList<>();
    public static ArrayList<Location> parkourStart = new ArrayList<>();
    public static ArrayList<Location> parkourEnd = new ArrayList<>();
    public static HashMap<UUID, Long> parkourPlayers = new HashMap<>();
    public static SkyWarsKitManager skyWarsKitManager;
    public static SkyWarsShop skyWarsShop;
    public static Cosmetics cosmetics;
    public static ArrayList<String> toggled = new ArrayList<>();
    ArrayList<UUID> uuidsWaiting = new ArrayList<>();

    public Hub(RebornCore rebornCore) {
        super("Hub", "hub", rebornCore, network.reborn.core.API.Module.HUB);
        network.reborn.core.Listeners.PlayerMove.setDoAfkStuff(false); // Don't do AFK stuff in hub...
        spawns.put(network.reborn.core.API.Module.HUB, new Location(Bukkit.getWorld("world"), -308.5, 18, 59.5, 0, 0));
//		spawns.put(network.reborn.core.API.Module.HUB, new Location(Bukkit.getWorld("world"), 0.5, 102, 1.5, 0, 0));
//		spawns.put(network.reborn.core.API.Module.SKYWARS, new Location(Bukkit.getWorld("world"), 1048.5, 119, 1048.5, -180, 0));
        spawns.put(network.reborn.core.API.Module.SKYWARS, new Location(Bukkit.getWorld("world"), 1519.5, 111, 1500.5, -90, 0));
        spawns.put(network.reborn.core.API.Module.TACTICALASSAULT, new Location(Bukkit.getWorld("world"), 1519.5, 110, 2500.5, -90, 0));
        spawns.put(network.reborn.core.API.Module.ULTRA_HARDCORE, new Location(Bukkit.getWorld("world"), 519.5, 111, 500.5, -90, 0));
        spawns.put(network.reborn.core.API.Module.UHC_REDDIT, new Location(Bukkit.getWorld("world"), 428.5, 129.0, 1149.5, -90, 0));
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(this), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new WorldListeners(this), RebornCore.getRebornCore());
        Bukkit.getPluginManager().registerEvents(new PlayerMove(this), RebornCore.getRebornCore());

        skyWarsKitManager = new SkyWarsKitManager();

        skyWarsShop = new SkyWarsShop();

        serverSelector = new ServerSelector();

        // Setup items in server selector

        ServerSelectorItem sw = new ServerSelectorItem("SkyWars", Material.BOW, (short) 0, "Take to the Sky to fight some of the top PVP players!", ServerSelectorItemType.GAME);
        sw.setServerNameORPrefix("SW");
        serverSelector.addItem(sw);

        ServerSelectorItem ta = new ServerSelectorItem("Tactical Assault", Material.FEATHER, (short) 0, "Fight your way into enemy bases to take out their banners!", ServerSelectorItemType.SECRET);
        ta.setServerNameORPrefix("TA");
        serverSelector.addItem(ta);

        ServerSelectorItem factions = new ServerSelectorItem("Factions", Material.TNT, (short) 0, "Gather your friends and build the biggest and strongest Faction!", ServerSelectorItemType.SOON);
        factions.setServerNameORPrefix("Factions");
//		serverSelector.addItem(factions);

        ServerSelectorItem pp = new ServerSelectorItem("Port Protector", Material.ROTTEN_FLESH, (short) 0, "Fight through rounds of monsters!", ServerSelectorItemType.GAME);
        pp.setServerNameORPrefix("PP");
//		serverSelector.addItem(pp);

        ServerSelectorItem kitPVP = new ServerSelectorItem("REKTTT", Material.DIAMOND_CHESTPLATE, (short) 0, "Just wait and stop trying to work this text out ;P", ServerSelectorItemType.SECRET);
        kitPVP.setServerNameORPrefix("KitPVP");
//		serverSelector.addItem(kitPVP);

        ServerSelectorItem uhc = new ServerSelectorItem("Ultra Hardcore", Material.GOLDEN_APPLE, (short) 0, "Good at playing survival? What about Ultra Hardcore?!", ServerSelectorItemType.GAME);
        uhc.setServerNameORPrefix("UHC");
        serverSelector.addItem(uhc);

        ServerSelectorItem FFA = new ServerSelectorItem("Free for All", Material.DIAMOND_SWORD, (short) 1, "Fight in a global FFA battle!", ServerSelectorItemType.GAME);
        FFA.setServerNameORPrefix("FFA");
//		serverSelector.addItem(FFA);

        ServerSelectorItem reddit = new ServerSelectorItem("Reddit UHC", Material.GOLDEN_APPLE, (short) 1, "Take UHC to another level by competing against players from Reddit in scheduled matches with custom scenarios!", ServerSelectorItemType.GAME);
        reddit.setServerNameORPrefix("UHC-REDDIT");
        serverSelector.addItem(reddit);

        ServerSelectorItem smp = new ServerSelectorItem("SMP", Material.MAP, (short) 1, "Play with other players on our survival server!" + "\n" + "\n" + ChatColor.RED + "Warning:    " + "Requires Minecraft 1.11.2", ServerSelectorItemType.SOLO);
        smp.setServerNameORPrefix("SMP");
        serverSelector.addItem(smp);
        serverSelector.setupServerSelector();

        Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), () -> {
            HashMap<network.reborn.core.API.Module, Integer> is = new HashMap<>();
            for (Map.Entry<Location, network.reborn.core.API.Module> map : signs.entrySet()) {
                //Bukkit.getLogger().info("SIGN FOR MODULE| LOC:[" + map.getKey().toString() + "]  MODULE:[" + map.getValue().toString() + "]");
                int i = 0;
                if (is.containsKey(map.getValue())) {
                    i = is.get(map.getValue()) + 1;
                }
                is.put(map.getValue(), i);
//				Bukkit.broadcastMessage("I - " + i);
//				Bukkit.broadcastMessage("XYZ - " + map.getKey().getBlockX() + " " + map.getKey().getBlockY() + " " + map.getKey().getBlockZ());
                ArrayList<CoveServer> servers = RebornCore.getServers(map.getValue(), GameState.WAITING);
                Sign signData = (Sign) map.getKey().getBlock().getState();
                Block block = map.getKey().getBlock().getLocation().add(0, 10, 0).getBlock();
                if (!block.getType().toString().contains("SIGN")) {
                    continue;
                }
                Sign sign = (Sign) block.getState();
                BlockFace blockFace = BlockFace.EAST;
                Block behind = sign.getBlock().getRelative(blockFace);
                if (servers.isEmpty() || i > servers.size() - 1) {
                    sign.setLine(0, "");
                    sign.setLine(1, "Searching for");
                    sign.setLine(2, "Server...");
                    sign.setLine(3, "");
                    sign.update(true);

                    if (behind.getType() != null && behind.getType() == Material.STAINED_GLASS) {
                        behind.setData((byte) 0);
                    }

                    signData.setLine(2, "");
                    signData.setLine(3, "");
                    signData.update(true);
                    continue;
                }
                CoveServer coveServer = servers.get(i);
                //				signData.setLine(0, "[SIGN]");
//				signData.setLine(1, map.getValue().toString());
                signData.setLine(2, "");
                signData.setLine(3, String.valueOf(coveServer.getID()));
                signData.update(true);

                sign.setLine(0, ChatColor.DARK_PURPLE + "[JOIN]");
                sign.setLine(1, map.getValue().getNiceName());
                sign.setLine(2, coveServer.getPlayers() + "/" + coveServer.getMaxPlayers());
                String line3 = "";
                if (coveServer.getExtraData().containsKey("Map"))
                    line3 = coveServer.getExtraData().get("Map").toString();
                sign.setLine(3, line3);
                sign.update(true);
                if (behind.getType() != null && behind.getType() == Material.STAINED_GLASS) {
                    behind.setData((byte) 5);
                }
            }
        }, 5L, 5L);

        Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), () -> {
            for (Location location : beacons) {
                RebornCore.getCoveAPI().getOnlineCovePlayers().stream().filter(covePlayer -> lobbies.containsKey(covePlayer.getUUID()) && lobbies.get(covePlayer.getUUID()) == network.reborn.core.API.Module.ULTRA_HARDCORE).forEach(covePlayer -> {
                    covePlayer.getPlayer().sendBlockChange(location, Material.STAINED_GLASS, (byte) covePlayer.getServerRank().getBlockColor());
                });
            }
        }, 5L, 5L);

        registerCosmetics();
    }

    public static void syncPlayersBasedOnLobby() {
        Bukkit.getOnlinePlayers().forEach(Hub::syncPlayerBasedOnLobby);
    }

    public static void syncPlayerBasedOnLobby(Player player) {
        syncPlayerBasedOnLobby(player, true);
    }

    public static void syncPlayerBasedOnLobby(Player player, boolean showPlayers) {
        network.reborn.core.API.Module lobby = network.reborn.core.API.Module.HUB;
        if (lobbies.containsKey(player.getUniqueId()))
            lobby = lobbies.get(player.getUniqueId());
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (lobbies.containsKey(player2.getUniqueId()) && lobbies.get(player2.getUniqueId()) == network.reborn.core.API.Module.ULTRA_HARDCORE && false) {
                player2.setPlayerTime(18000L, false);
            } else {
                player2.setPlayerTime(6000L, false);
            }
            network.reborn.core.API.Module lobby2 = network.reborn.core.API.Module.HUB;
            if (lobbies.containsKey(player2.getUniqueId()))
                lobby2 = lobbies.get(player2.getUniqueId());
            if (lobby != lobby2) {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
            } else if (showPlayers) {
                player.showPlayer(player2);
                player2.showPlayer(player);
            }
        }
    }

    public void registerCosmetics() {
        cosmetics = new Cosmetics();

        BatBlaster batBlaster = new BatBlaster();
        cosmetics.addGadget(batBlaster);

        EnderPearlCannon enderPearlCannon = new EnderPearlCannon();
        cosmetics.addGadget(enderPearlCannon);

        PartyPopper partyPopper = new PartyPopper();
        cosmetics.addGadget(partyPopper);

        RailGun railGun = new RailGun();
        cosmetics.addGadget(railGun);

//		ChristmasTree christmasTree = new ChristmasTree(); (Doesn't work in 1.10 cause of particles?)
//		cosmetics.addGadget(christmasTree); // Christmas Tree Gadget

        SnowballCannon snowballCannon = new SnowballCannon();
        cosmetics.addGadget(snowballCannon);

        SmashDown smashDown = new SmashDown();
        cosmetics.addGadget(smashDown);

        ExplosiveSheep explosiveSheep = new ExplosiveSheep();
        cosmetics.addGadget(explosiveSheep);

        DiscoBall discoBall = new DiscoBall();
        cosmetics.addGadget(discoBall);

        // All below gadgets will NOT be released on launch and will be added at a later date

//		Parachute parachute = new Parachute();
//		parachute.setPrivateBeta(true);
//		cosmetics.addGadget(parachute);

//		PoopBomb poopBomb = new PoopBomb();
//		poopBomb.setPrivateBeta(false);
//		cosmetics.addGadget(poopBomb);

        // TODO Fix the below, doesn't work as intended
//		BlackHole blackHole = new BlackHole();
//		blackHole.setPrivateBeta(true);
//		cosmetics.addGadget(blackHole);
//
//		Tsunami tsunami = new Tsunami();
//		tsunami.setPrivateBeta(true);
//		cosmetics.addGadget(tsunami);

        // Test Hats
        // ConnorLinfoot Hat
        Hat connorlinfoot = new Hat("ElectronicWizard", "electronicwizard", Material.SKULL_ITEM, "ElectronicWizard");
        cosmetics.addHat(connorlinfoot);

        // Notch Hat
        Hat notch = new Hat("Notch", "notch", Material.SKULL_ITEM, "Notch");
        cosmetics.addHat(notch);

        // Dinnerbone Hat
        Hat dinnerbone = new Hat("Dinnerbone", "dinnerbone", Material.SKULL_ITEM, "Dinnerbone");
        cosmetics.addHat(dinnerbone);

        // Test Morphs
        Skeleton skeleton = new Skeleton("Skeleton", "skeleton", Material.BONE, DisguiseType.SKELETON);
        cosmetics.addMorph(skeleton);
    }

    public void onDisable() {
        for (Map.Entry<Location, network.reborn.core.API.Module> map : signs.entrySet()) {
            Sign sign = (Sign) map.getKey().getBlock().getState();
            sign.setLine(0, "[SIGN]");
            sign.setLine(1, map.getValue().toString());
            sign.setLine(2, "");
            sign.setLine(3, "");
            sign.update(true);
        }
//		for (Location location : beacons) {
//			location.getBlock().setType(Material.SIGN);
//			Sign sign = (Sign) location.getBlock().getState();
//			sign.setLine(0, "[BEACON]");
//		}
        Bukkit.getWorld("world").save();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.HUB));
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        event.getPlayer().setFlying(false);
        event.getPlayer().setAllowFlight(false);

        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setArmorContents(null);

        hidePlayerFromHidden(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() < 104 && event.getPlayer().getGameMode() != GameMode.CREATIVE && lobbies.containsKey(event.getPlayer().getUniqueId()) && lobbies.get(event.getPlayer().getUniqueId()) != network.reborn.core.API.Module.HUB)
            teleportPlayerBasedOnLobby(event.getPlayer());
        else if (event.getTo().getY() < 0)
            teleportPlayerBasedOnLobby(event.getPlayer());
        else if (event.getTo().getY() > 50 && lobbies.containsKey(event.getPlayer().getUniqueId()) && lobbies.get(event.getPlayer().getUniqueId()) == network.reborn.core.API.Module.HUB)
            teleportPlayerBasedOnLobby(event.getPlayer());

        if ((event.getTo().getBlock().getType() == Material.ENDER_PORTAL && event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getFrom().getBlock().getType() != Material.ENDER_PORTAL) || (event.getTo().getBlock().getType() == Material.STONE_PLATE && event.getFrom().getBlock().getType() != Material.STONE_PLATE)) {
            teleportPlayerBasedOnLobby(event.getPlayer());

            // Put player in game based on lobby
            network.reborn.core.API.Module module = lobbies.get(event.getPlayer().getUniqueId());
            if (module != null && module != network.reborn.core.API.Module.HUB) {
                RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(event.getPlayer());
                rebornPlayer.sendTitle(ChatColor.GREEN + "Finding Server...", ChatColor.AQUA + "Please wait...", 10, 200, 0);
                if (!uuidsWaiting.contains(event.getPlayer().getUniqueId())) {
                    sendToServer(event.getPlayer(), module);
                    uuidsWaiting.add(event.getPlayer().getUniqueId());
                }
            }

//			Location location = event.getTo().clone();
//			location.setY(90);
//			if (location.getBlock().getType().toString().contains("SIGN")) {
//				Sign sign = (Sign) location.getBlock().getState();
//				if (sign.getLine(0).equalsIgnoreCase("[JOIN]")) {
//					RebornPlayer covePlayer = RebornCore.getRebornAPI().getCovePlayer(event.getPlayer());
//					covePlayer.sendTitle(ChatColor.GREEN + "Finding Server...", ChatColor.AQUA + "Please wait...", 10, 200, 0);
//					if (!uuidsWaiting.contains(event.getPlayer().getUniqueId())) {
//						sendToServer(event.getPlayer(), network.reborn.core.API.Module.SKYWARS);
//						uuidsWaiting.add(event.getPlayer().getUniqueId());
//					}
//				}
//			}
        }
    }

    public void sendToServer(Player player, network.reborn.core.API.Module module) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(RebornCore.getRebornCore(), () -> {
            RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
            rebornPlayer.clearTitle();
            ArrayList<CoveServer> servers = RebornCore.getServers(module, GameState.WAITING);
            if (servers.isEmpty()) {
                rebornPlayer.sendTitle(ChatColor.GREEN + "Finding Server...", ChatColor.AQUA + "Please wait...", 0, 200, 10);
                Bukkit.getScheduler().runTaskLaterAsynchronously(RebornCore.getRebornCore(), () -> sendToServer(player, module), 20L);
                return;
            }
            int id = servers.get(0).getID();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(String.valueOf(id));
            player.sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out.toByteArray());
            uuidsWaiting.remove(player.getUniqueId());
        }, 0L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (hidden.contains(event.getPlayer().getUniqueId()))
            hidden.remove(event.getPlayer().getUniqueId());
        if (uuidsWaiting.contains(event.getPlayer().getUniqueId()))
            uuidsWaiting.remove(event.getPlayer().getUniqueId());
        if (lobbies.containsKey(event.getPlayer().getUniqueId()))
            lobbies.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRealPlayerMove(RealPlayerMoveEvent event) {
        if (event.getEvent().getTo().getBlock().getType().toString().contains("WATER")) {
            Location location = event.getEvent().getTo().clone();
            location.setY(100);
            if (location.getBlock().getType().toString().contains("SIGN")) {
                Sign sign = (Sign) location.getBlock().getState();
                if (sign.getLine(0).equals("[GAME]")) {
                    switch (sign.getLine(1)) {
                        default:
                            break;
                        case "SKYWARS":
                            event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.SKYWARS));
                            lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.SKYWARS);
                            syncPlayersBasedOnLobby();
                            break;
                        case "PORT_PROTECTOR":
                            event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.PORT_PROTECTOR));
                            lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.PORT_PROTECTOR);
                            syncPlayersBasedOnLobby();
                            break;
                        case "ULTRAHARDCORE":
                            event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.ULTRA_HARDCORE));
                            lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.ULTRA_HARDCORE);
                            syncPlayersBasedOnLobby();
                            break;
                        case "UHCREDDIT":
                            event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.UHC_REDDIT));
                            lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.UHC_REDDIT);
                            syncPlayersBasedOnLobby();
                            break;
                    }
                } else if (sign.getLine(0).equals("[HUB]")) {
                    event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.HUB));
                    lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.HUB);
                    syncPlayersBasedOnLobby();
                }
            }

            location = event.getEvent().getTo().clone();
            location.setY(92);
            if (location.getBlock().getType().toString().contains("SIGN")) {
                Sign sign = (Sign) location.getBlock().getState();
                if (sign.getLine(0).equals("[HUB]")) {
                    event.getPlayer().teleport(spawns.get(network.reborn.core.API.Module.HUB));
                    lobbies.put(event.getPlayer().getUniqueId(), network.reborn.core.API.Module.HUB);
                    syncPlayersBasedOnLobby();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getInventory().getName().contains("Lobby Selector") || event.getInventory().getName().contains("Player:")) {
            ItemStack clicked = event.getCurrentItem();
            Inventory inventory = event.getInventory();
            String clickedName = clicked.getItemMeta().getDisplayName();
            if (clickedName.contains("SkyWars")) {
                event.getWhoClicked().teleport(spawns.get(network.reborn.core.API.Module.SKYWARS));
                lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.SKYWARS);
                syncPlayersBasedOnLobby();
            } else if (clickedName.contains("Hub")) {
                event.getWhoClicked().teleport(spawns.get(network.reborn.core.API.Module.HUB));
                lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.HUB);
                syncPlayersBasedOnLobby();
            } else if (clickedName.contains("Ultra Hardcore")) {
                event.getWhoClicked().teleport(spawns.get(network.reborn.core.API.Module.ULTRA_HARDCORE));
                lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.ULTRA_HARDCORE);
                syncPlayersBasedOnLobby();
            } else if (clickedName.contains("Port Protector")) {
                event.getWhoClicked().teleport(spawns.get(network.reborn.core.API.Module.PORT_PROTECTOR));
                lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.ULTRA_HARDCORE);
                syncPlayersBasedOnLobby();
            } else if (clickedName.contains("Tactical Assault")) {
                event.getWhoClicked().teleport(Hub.spawns.get(network.reborn.core.API.Module.TACTICALASSAULT));
                Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.TACTICALASSAULT);
                Hub.syncPlayersBasedOnLobby();
            } else if (clickedName.contains("Reddit")) {
                event.getWhoClicked().teleport(Hub.spawns.get(network.reborn.core.API.Module.UHC_REDDIT));
                Hub.lobbies.put(event.getWhoClicked().getUniqueId(), network.reborn.core.API.Module.UHC_REDDIT);
                Hub.syncPlayersBasedOnLobby();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        network.reborn.core.API.Module lobby = network.reborn.core.API.Module.HUB;
        if (lobbies.containsKey(event.getPlayer().getUniqueId()))
            lobby = lobbies.get(event.getPlayer().getUniqueId());
        ArrayList<Player> remove = new ArrayList<>();
        for (Player player2 : event.getRecipients()) {
            if (player2.getUniqueId() == event.getPlayer().getUniqueId())
                continue;
            network.reborn.core.API.Module lobby2 = network.reborn.core.API.Module.HUB;
            if (lobbies.containsKey(player2.getUniqueId()))
                lobby2 = lobbies.get(player2.getUniqueId());
            if (lobby != lobby2)
                remove.add(player2);
        }

        for (Player player : remove)
            event.getRecipients().remove(player);
    }

    public void teleportPlayerBasedOnLobby(Player player) {
        network.reborn.core.API.Module module = network.reborn.core.API.Module.HUB;
        if (lobbies.containsKey(player.getUniqueId()))
            module = lobbies.get(player.getUniqueId());
        switch (module) {
            default:
                player.teleport(spawns.get(module));
                break;
            case HUB:
                player.teleport(spawns.get(network.reborn.core.API.Module.HUB));
                break;
        }
    }

    @EventHandler
    public void onPlayerDamage(PlayerDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        teleportPlayerBasedOnLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }


    //TODO: Reenable for 1.9+
    /*@EventHandler
	public void onItemSwap(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true); // Stop off hand in hub
	}*/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getType() != null && event.getClickedInventory().getType() == InventoryType.PLAYER && event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    public void showPlayers(Player player) {
        ItemStack hider = new ItemStack(Material.INK_SACK, 1, (short) 10);
        ItemMeta hiderMeta = hider.getItemMeta();
        hiderMeta.setDisplayName(ChatColor.WHITE + "Players: " + ChatColor.GREEN + "On");
        hider.setItemMeta(hiderMeta);
        player.getInventory().setItem(8, hider);

        if (hidden.contains(player.getUniqueId()))
            hidden.remove(player.getUniqueId());

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId() == player1.getUniqueId())
                continue;
            player.showPlayer(player1);
        }
        syncPlayerBasedOnLobby(player, false);
    }

    public void hidePlayers(Player player) {
        ItemStack hider = new ItemStack(Material.INK_SACK, 1, (short) 8);
        ItemMeta hiderMeta = hider.getItemMeta();
        hiderMeta.setDisplayName(ChatColor.WHITE + "Players: " + ChatColor.RED + "Off");
        hider.setItemMeta(hiderMeta);
        player.getInventory().setItem(8, hider);

        hidden.add(player.getUniqueId());

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId() == player1.getUniqueId())
                continue;
            player.hidePlayer(player1);
        }
        syncPlayerBasedOnLobby(player, false);
    }

    // This method is called when a player joins to hide said player from players who have players hidden
    public void hidePlayerFromHidden(Player player) {
        syncPlayerBasedOnLobby(player, true);
        for (UUID uuid : hidden) {
            Player player1 = Bukkit.getPlayer(uuid);
            if (player1 == null)
                continue;
            player1.hidePlayer(player);
        }
        syncPlayerBasedOnLobby(player, false);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true); // Disable weather
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true); // Disable item drops
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getLocation().add(0, -10, 0).getBlock().getType().toString().contains("SIGN")) {
            Sign sign = (Sign) event.getClickedBlock().getLocation().add(0, -10, 0).getBlock().getState();
            if (sign.getLine(3).isEmpty()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Searching for server...");
            } else {
                String server = sign.getLine(3);
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                event.getPlayer().sendPluginMessage(RebornCore.getRebornCore(), "BungeeCord", out.toByteArray());
            }
        }
    }

}
