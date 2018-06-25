package network.reborn.core.Util;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import network.reborn.core.Events.PlayerDamageEvent;
import network.reborn.core.Module.Games.Game;
import network.reborn.core.Module.Games.Team;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.Scatter.DefaultScatterer;
import network.reborn.core.Util.Scatter.Scatterer;
import network.reborn.core.Util.Scatter.exceptions.ScatterLocationException;
import network.reborn.core.Util.Scatter.logic.RandomSquareScatterLogic;
import network.reborn.core.Util.Scatter.zones.CircularDeadZoneBuilder;
import network.reborn.core.Util.Scatter.zones.DeadZone;
import network.reborn.core.Util.Scatter.zones.SquareDeadZoneBuilder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.*;

public class ScatterEngine implements Listener {

    /**
     * This method will load a list of safe scatter locations in the specified
     * world then prepare the Scheduler to teleport all ingame Players and Teams
     * into that world.
     *
     * @param w
     * World to load scatter locations in.
     * @param radius
     * Radius to be used for calculations.
     */
    public static Boolean isScattered = false;
    public static HashMap<String, Location> toScatterLater = new HashMap<String, Location>();
    public static int k = 0;
    public static int m = 0;
    public static int a = 0;
    public static int b = 0;
    public static Boolean permitDamage = true;

    public static void resetToDefault() {
        isScattered = false;
        toScatterLater = new HashMap<>();
        Bukkit.getScheduler().cancelTask(k);
        Bukkit.getScheduler().cancelTask(m);
        Bukkit.getScheduler().cancelTask(a);
        Bukkit.getScheduler().cancelTask(b);
        k = 0;
        m = 0;
        a = 0;
        b = 0;
        permitDamage = true;
    }

    public static void startScatter(Game game, World w, double radius, int minDistance,
                                    ScatterType state) {
        permitDamage = false;
        try {
            ArrayList<Material> m = new ArrayList<Material>();
            if (state.equals(ScatterType.GAME)) {
                m.add(Material.STAINED_CLAY);
                m.add(Material.GRASS);
                m.add(Material.LONG_GRASS);
                m.add(Material.SAND);
                m.add(Material.SNOW);
                m.add(Material.SNOW_BLOCK);
            }
            if (state.equals(ScatterType.MEETUP)) {
                m.add(Material.GRASS);
                m.add(Material.LEAVES);
                m.add(Material.LEAVES_2);
            }

            Location st = new Location(w, 0.0, 2.0, 0.0);
            try {
                Location test = st.getWorld().getHighestBlockAt(st).getLocation();
            } catch (Exception e) {
                Bukkit.getServer().createWorld(new WorldCreator("game"));
            }
            Location s = st.getWorld().getHighestBlockAt(st).getLocation();
            RandomSquareScatterLogic logic = new RandomSquareScatterLogic(
                    new Random(), s, 100, radius - 5.0,
                    m.toArray(new Material[m.size()]));
            List<DeadZone> deadZones = new ArrayList<DeadZone>();
            SquareDeadZoneBuilder builder = new SquareDeadZoneBuilder(
                    radius - 5.0);

            DeadZone spawnArea = builder.buildForLocation(s);
            deadZones.add(spawnArea);

            CircularDeadZoneBuilder deadZoneForTeleports = new CircularDeadZoneBuilder(
                    minDistance);

            Scatterer sc = new DefaultScatterer(logic, deadZones,
                    deadZoneForTeleports);

            List<Location> loc = sc
                    .getScatterLocations(Bukkit.getOnlinePlayers().size() + 5);
            scatterFromList(game, loc, state);
        } catch (ScatterLocationException e) {
            Bukkit.broadcastMessage(ChatColor.RED
                    + "An unknown error occured while loading scatter locations.");
            ErrorDump ed = new ErrorDump(ScatterEngine.class.getName(), e);
            ed.createSpigot();

        }
    }

    /**
     * Using the provided list, this method will actually schedule all tasks to
     * perform the scatters
     *
     * @param l List of safe scatter locations to be used
     */
    @SuppressWarnings("deprecation")
    private static void scatterFromList(Game game, List<Location> l, ScatterType state) {
        ArrayList<ScatterableObject> toScatter = new ArrayList<ScatterableObject>();
        ArrayList<Team> listed = new ArrayList<Team>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isOnTeam(p, game)) {
                toScatter.add(new ScatterableObject(p));
            } else {
                Team t = game.getPlayerTeam(p);
                if (!listed.contains(t)) {
                    listed.add(t);
                    toScatter.add(new ScatterableObject(t));
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                int i = 1;
                int s = l.size();
                bc(ChatColor.YELLOW + "Beginning chunk preload. Chunks to load: "
                        + ChatColor.GREEN + s);
                for (Location loc : l) {
                    schedule(i, loc, s, toScatter, l, state);
                    i++;
                    /*if (i > s) {
                        isScattered = true;
                        break;
                    }*/
                }
            }
        }, 60L);

    }

    static void bc(String msg) {
        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Scatter" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.YELLOW + msg);
    }


    static void schedule(int i, Location loc, int s,
                         ArrayList<ScatterableObject> toScatter, List<Location> l,
                         ScatterType state) {
        Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                if (!loc.getChunk().isLoaded()) {
                    loc.getChunk().load();
                }
                bc(ChatColor.GRAY
                        + "Loaded chunk " + ChatColor.YELLOW + i + "/" + s);
            }
        }, Long.parseLong("" + 20 * i));
        if (i == s) {
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(),
                    new Runnable() {
                        @Override
                        public void run() {
                            bc(
                                    ChatColor.YELLOW
                                            + "Chunk preload complete. Beginning scatter. Players/Teams to scatter: "
                                            + ChatColor.GREEN + toScatter.size());
                            RebornCore.getCoveAPI().getGame().freezeAllPlayers();
                            ss(toScatter, l, state);
                        }
                    }, Long.parseLong("" + 20 * (i + 1)));
        }

    }

    static void ss(ArrayList<ScatterableObject> toScatter, List<Location> l,
                   ScatterType state) {
        toScatterLater = new HashMap<String, Location>();
        b = Bukkit.getScheduler().scheduleSyncRepeatingTask(RebornCore.getRebornCore(),
                () -> {
                    int i = toScatter.size() - 1;
                    Boolean scattered = false;
                    if (m <= i) {
                        ScatterableObject o = toScatter.get(m);
                        if (o.isPlayer()) {
                            if (o.getPlayer().isOnline()) {
                                Player p = (Player) o.getPlayer();
                                p.teleport(l.get(m).add(0.0, 2.0, 0.0));
                                p.sendMessage(
                                        ChatColor.AQUA
                                                + "Scattered you to "
                                                + ChatColor.GREEN
                                                + LocationUtil
                                                .locationAsString(
                                                        l.get(m)));
                                freezeForMC18(p);
                                scattered = true;
                                if (state.equals(ScatterType.GAME)) {
                                    p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                                }
                            } else {
                                toScatterLater.put(o.getPlayer().getName(),
                                        l.get(m).add(0.0, 2.0, 0.0));
                                scattered = false;
                            }

                        } else if (o.isTeam()) {
                            Team t = o.getTeam();
                            for (UUID op : t.getPlayers()) {
                                if (Bukkit.getPlayer(op).isOnline()) {
                                    Player tp = Bukkit.getPlayer(op);
                                    tp.teleport(
                                            l.get(m).add(0.0, 2.0, 0.0));
                                    tp.sendMessage(
                                            ChatColor.AQUA
                                                    + "Scattered your team to "
                                                    + ChatColor.GREEN
                                                    + LocationUtil.locationAsString(
                                                    l.get(m)));
                                    freezeForMC18(tp);
                                    if (state.equals(ScatterType.GAME)) {
                                        tp.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                                    }
                                } else {
                                    toScatterLater.put(Bukkit.getPlayer(op).getName(),
                                            l.get(m).add(0.0, 2.0, 0.0));
                                }
                            }
                            scattered = true;
                        }
                        if (scattered) {
                            bc(
                                    ChatColor.GRAY + "Scattered "
                                            + ChatColor.YELLOW + (m + 1) + "/"
                                            + (i + 1));
                        } else {
                            bc(
                                    ChatColor.GRAY
                                            + "Player not online; scheduling late-scatter.");
                        }
                        m++;
                        if (m > i) {
                            permitDamage = true;
                            isScattered = true;
                        }
                    }
                }, 60L, 60L);
    }

    /**
     * Converts specified radian input to Minecraft-friendly block value
     *
     * @param //radius Radius
     * @param //angle  Angle
     * @return Converted block value
    /*     *//*
          private static int[] convertFromRadiansToBlock(double radius, double
		  angle) { return new int[] { (int) Math.round(radius 
		  Math.cos(angle)), (int) Math.round(radius  Math.sin(angle)) }; }
		 
		  public static void freeze(Player player) { l("0");
		  Location location = player.getLocation(); l("1");
		  final DataWatcherObject<Byte> ax = DataWatcher.a(EntityPlayer.class, DataWatcherRegistry.a); l("2");
		  try {
		      PacketPlayOutSpawnEntityLiving pkt = new PacketPlayOutSpawnEntityLiving(); l("3");
		      setField(pkt, Integer.TYPE, 0, Integer.valueOf(538085));
	//	      l("4");
		      setField(pkt, Integer.TYPE, 1, Byte.valueOf((byte) 65));
	//	      l("5");
              setField(pkt, Integer.TYPE, 2, Integer.valueOf((int) Math.floor(location.getX() * 32.0D)));
  //            l("6");
              setField(pkt, Integer.TYPE, 3, Integer.valueOf((int) Math.floor(location.getY() * 32.0D)));
//              l("7");
              setField(pkt, Integer.TYPE, 4, Integer.valueOf((int) Math.floor(location.getZ() * 32.0D)));
              //l("8");
              DataWatcher w = ((CraftPlayer) player).getHandle().getDataWatcher(); l("9");
		 
		  // w.a(0, Byte.valueOf((byte) 32)); //instead of this line
		 
		   w.register(ax, Byte.valueOf((byte) 0)); if (w == null) {
		  Bukkit.getLogger().info("'w' is null"); }
		  if (ax == null) {
		  Bukkit.getLogger().info("'ax' is null"); }
		  w.set(ax, Byte.valueOf((byte) 32)); l("10");
		  setField(pkt, DataWatcher.class, 0, w); l("11");
		 
		  PacketPlayOutAttachEntity attachPacket = new
		  PacketPlayOutAttachEntity(); l("12"); setField(attachPacket,
		  Integer.TYPE, 0, Integer.valueOf(0)); l("13"); setField(attachPacket,
		  Integer.TYPE, 1, Integer.valueOf(player.getEntityId())); l("14");
		  setField(attachPacket, Integer.TYPE, 2, Integer.valueOf(538085));
		  l("15"); ((CraftPlayer)
		  player).getHandle().playerConnection.sendPacket(pkt); l("16");
		  ((CraftPlayer) player).getHandle().playerConnection
		  .sendPacket(attachPacket); l("17"); }catch(
		 
		  Exception e) { ErrorDump ed = new
		  ErrorDump(ScatterEngine.class.getName(), e); ed.createSpigot(); } }
		 
		  public static void unfreeze(Player player) { ((CraftPlayer)
		  player).getHandle().playerConnection.sendPacket( new
		  PacketPlayOutEntityDestroy(new int[] { 538085 })); }
		 
		  private static void setField(Object object, Class<?> type, int index,
		  Object value) throws NoSuchFieldException, IllegalAccessException {
		  int i = 0; for (Field field : object.getClass().getDeclaredFields())
		  { if ((field.getType().equals(type)) && (i++ == index)) {
		  field.setAccessible(true); field.set(object, value); break; } } }*/
    @SuppressWarnings("unused")
    private static int getSafeY(Location loc) {
        return loc.getWorld().getHighestBlockYAt(loc);
    }

    /**
     * Gets the safe Y-value at a location
     *
     * @param //loc Location to be tested
     * @return Safe Y-value
     */
    /*
	 * private static int getSafeY(Location loc) { return
	 * loc.getWorld().getHighestBlockYAt(loc); }
	 *
	 */
    @SuppressWarnings("unused")
    private static int[] convertFromRadiansToBlock(double radius,
                                                   double angle) {
        return new int[]{(int) Math.round(radius * Math.cos(angle)),
                (int) Math.round(radius * Math.sin(angle))};
    }

/*	public static void m() {
        Bukkit.getScheduler().cancelTask(b);
		a = 0;
		k = 0;
		b = 0;
		m = 0;
		Bukkit.broadcastMessage( + ChatColor.GREEN
				+ "All players/teams have been scattered. Starting meetup.");
		GameSystem.runningTask2 = Bukkit.getScheduler()
				.scheduleSyncDelayedTask(RebornCore.getRebornCore(), new Runnable() {
					@Override
					public void run() {
						Bukkit.broadcastMessage( + ChatColor.RED
								+ "" + ChatColor.BOLD + "Meetup start!");
						Events.globalInvincible = false;
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								"ffg off");
						MultiverseWorld mvmu = Core.core.getMVWorldManager()
								.getMVWorld("meetup");
						mvmu.setRespawnToWorld("world");
						mvmu.setEnableWeather(false);
						mvmu.setTime("day");
					}

				}, 40L);
		GameSystem.runningTask3 = Bukkit.getScheduler()
				.scheduleSyncDelayedTask(RebornCore.getRebornCore(), new Runnable() {

					@Override
					public void run() {
						Bukkit.broadcastMessage(
								+ ChatColor.YELLOW + "" + ChatColor.BOLD
								+ "WorldBorder is now shrinking to 10x10 for next 10 minutes.");
						Bukkit.getWorld("meetup").getWorldBorder().setSize(20.0,
								600L);
					}

				}, 12000L);
	}

	public static void z() {
		Bukkit.getScheduler().cancelTask(b);
		a = 0;
		k = 0;
		b = 0;
		m = 0;
		Bukkit.broadcastMessage( + ChatColor.GREEN
				+ "All players/teams have been scattered. Starting game.");
		String gt = GameSystem.milliToStandard(
				Long.parseLong("" + (GameSystem.initialMU * 1000)));
		GameSystem.sendStartMessage(gt);
		final Boolean veg = io.uhc.reborn.uhc.scenarios.vegetarian.Main
				.isRunning();
		final Boolean fishing = io.uhc.reborn.uhc.scenarios.gonefishing.Main
				.isRunning();

		ItemStack rod = new ItemStack(Material.AIR);
		if (fishing) {
			rod = new ItemStack(Material.FISHING_ROD);
			rod.addUnsafeEnchantment(Enchantment.LUCK, 250);
			rod.addUnsafeEnchantment(Enchantment.LURE, 3);
			ItemMeta rm = rod.getItemMeta();
			rm.spigot().setUnbreakable(true);
			rod.setItemMeta(rm);
		}
		Player dispatcher = null;
		for (Player b : Bukkit.getOnlinePlayers()) {
			b.getInventory().clear();
			if (veg) {
				b.getInventory().addItem(new ItemStack(Material.BREAD, 10));
			} else {
				b.getInventory()
						.addItem(new ItemStack(Material.COOKED_BEEF, 10));
			}
			b.setLevel(0);
			b.setExp(Float.parseFloat("0"));
			if (fishing) {
				b.getInventory().addItem(rod);
				b.getInventory().addItem(new ItemStack(Material.ANVIL, 64));
				b.setLevel(9999999);
			}
			b.updateInventory();
			b.setHealth(18.0);
			b.setHealth(19.9);
			b.setFoodLevel(20);
			b.setSaturation(50);
			b.setMaxHealth(20.0);

			if (b.hasPermission("uhccore.use")) {
				dispatcher = b;
			}
		}
		io.uhc.reborn.uhc.scenarios.puppypower.Main.giveStartItems();
		// io.uhc.reborn.uhc.scenarios.deadmanwalking.Main.onGameStart();
		Bukkit.getWorld("uhc").setTime(0L);
		if (dispatcher == null) {
			Bukkit.broadcastMessage( + ChatColor.DARK_RED
					+ "No players online with permission " + ChatColor.YELLOW
					+ "uhccore.use" + ChatColor.DARK_RED
					+ ", game failed to start!");
			GameSystem.stopTimer();
			return;
		} else {
			Bukkit.dispatchCommand(dispatcher, "butcher 3000");
		}
	}*/

	/*
	 * static ArrayList<Player> scattered = new ArrayList<Player>(); static
	 * ArrayList<Player> toScatter = new ArrayList<Player>(); private static
	 * void scatter(List<Location> l, int iteration, int scatterCount,
	 * List<Player> solos, Boolean firstParse) { if (firstParse) {
	 * toScatter.addAll(Bukkit.getOnlinePlayers()); if (solos.size() == 0) {
	 * scatter(l, iteration, scatterCount, solos, false); } Player s =
	 * solos.get(0); s.sendMessage(Strings.getSpigotPrefix() + ChatColor.AQUA +
	 * "Scattering you to " + ChatColor.GRAY +
	 * LocationUtil.locationAsString(l.get(iteration)));
	 * s.teleport(l.get(iteration).add(0.0, 2.0, 0.0)); solos.remove(0);
	 * scattered.add(s); } else { if (toScatter.size() == 0) { return; } Player
	 * s = toScatter.get(0); if (scattered.contains(s)) { scatter(l,
	 * iteration++) return; }
	 * 
	 * } }
	 */

    public static void freezeForMC18(Player player) {
        Location location = player.getLocation();
        try {
            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving();
            setField(spawnPacket, Integer.TYPE, 0, Integer.valueOf(538085));
            setField(spawnPacket, Integer.TYPE, 1, Byte.valueOf((byte) 65));
            setField(spawnPacket, Integer.TYPE, 2,
                    Integer.valueOf((int) Math.floor(location.getX() * 32.0D)));
            setField(spawnPacket, Integer.TYPE, 3,
                    Integer.valueOf((int) Math.floor(location.getY() * 32.0D)));
            setField(spawnPacket, Integer.TYPE, 4,
                    Integer.valueOf((int) Math.floor(location.getZ() * 32.0D)));
            DataWatcher watcher = new DataWatcher(null);
            watcher.a(0, Byte.valueOf((byte) 32));
            setField(spawnPacket, DataWatcher.class, 0, watcher);

            PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity();
            setField(attachPacket, Integer.TYPE, 0, Integer.valueOf(0));
            setField(attachPacket, Integer.TYPE, 1,
                    Integer.valueOf(player.getEntityId()));
            setField(attachPacket, Integer.TYPE, 2, Integer.valueOf(538085));

            ((CraftPlayer) player).getHandle().playerConnection
                    .sendPacket(spawnPacket);
            ((CraftPlayer) player).getHandle().playerConnection
                    .sendPacket(attachPacket);
        } catch (Exception e) {
            ErrorDump ed = new ErrorDump(ScatterEngine.class.getName(), e);
            ed.createSpigot();
        }
    }

    public static void unfreeze(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutEntityDestroy(538085));
    }

    private static void setField(Object object, Class<?> type, int index,
                                 Object value) throws NoSuchFieldException, IllegalAccessException {
        int i = 0;
        for (Field field : object.getClass().getDeclaredFields()) {
            if ((field.getType().equals(type)) && (i++ == index)) {
                field.setAccessible(true);
                field.set(object, value);
                break;
            }
        }
    }

    /**
     * Determines if a specified player is on a team tracked by the Scoreboard
     *
     * @param v Player to be checked
     * @return If player is on team or not
     */
    @SuppressWarnings("deprecation")
    static Boolean isOnTeam(Player v, Game g) {
        Boolean r = false;
        if (g.getPlayerTeam(v) != null) {
            r = true;
        }
        return r;
    }

    static void l(String s) {
        Bukkit.getLogger().info("freeze() // LINE: [" + s + "]");
    }

    static void m(String s) {
        Bukkit.getLogger().info("setField() // LINE: [" + s + "]");
    }

/*    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!permitMovement) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
                event.setCancelled(true);
            }
        }
    }*/

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        if (toScatterLater.containsKey(name)) {
            final Location l = toScatterLater.get(name);
            final Player p = event.getPlayer();
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            toScatterLater.remove(name);
            bc(ChatColor.GRAY
                    + "Late-scattered " + ChatColor.YELLOW + name);
            Bukkit.getScheduler().runTaskLater(RebornCore.getRebornCore(),
                    new Runnable() {
                        @Override
                        public void run() {
                            p.getInventory().clear();
                            p.teleport(l);
                            p.sendMessage(ChatColor.AQUA
                                    + "Late-scattered you to " + ChatColor.GREEN
                                    + LocationUtil.locationAsString(l));
                            p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));

                        }
                    }, 40L);
        }
    }

    @EventHandler
    public void onDamage(PlayerDamageEvent event) {
        if (!permitDamage) {
            event.setCancelled(true);
        }
    }

}
