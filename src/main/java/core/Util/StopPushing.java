package network.reborn.core.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class StopPushing {
    private static String[] all = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static Random r = new Random();
    private static Class<?> ct = NMSUtils.getOBCClass("scoreboard.CraftTeam");
    private static Field t;
    private static Class<?> etp;
    private static Class<?> st;
    private static Method a;
    private static Method k;
    private static Object ALLOW;
    private static Object NEVER;
    private static boolean old;

/*	static {
        t = NMSUtils.getField(ct, "team");
		etp = NMSUtils.getNMSClassSilent("EnumTeamPush", "ScoreboardTeamBase");
		st = NMSUtils.getNMSClass("ScoreboardTeam");
		a = NMSUtils.getMethod(st, "a", new Class[]{etp});
		k = NMSUtils.getMethod(st, "k", new Class[0]);
		old = false;

		try {
			Class e = NMSUtils.getInnerClassSilent(Team.class, "Option");
			if (e == null) {
				old = true;
				ct = NMSUtils.getOBCClass("scoreboard.CraftTeam");
				t = NMSUtils.getField(ct, "team");
				etp = NMSUtils.getNMSClassSilent("EnumTeamPush", "ScoreboardTeamBase");
				st = NMSUtils.getNMSClass("ScoreboardTeam");
				a = NMSUtils.getMethod(st, "a", new Class[]{etp});
				ALLOW = etp.getEnumConstants()[0];
				NEVER = etp.getEnumConstants()[1];
			}
		} catch (Exception var1) {
			var1.printStackTrace();
		}
	}

	public StopPushing() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(RebornCore.getRebornCore(), () -> {
			Bukkit.getOnlinePlayers().forEach(this::set);
		}, 200L, 200L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.set(event.getPlayer());
	}

	private void set(Team team, Player player) {
		if (player.hasPermission("stoppushing.allow") && false) { // Do some stuff here soon
			if (!team.getOption(Team.Option.COLLISION_RULE).equals(Team.OptionStatus.ALWAYS)) {
				team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
			}
//		} else if (!team.getOption(Team.Option.COLLISION_RULE).equals(Team.OptionStatus.NEVER)) {
		} else {
			RebornPlayer covePlayer = RebornCore.getRebornAPI().getCovePlayer(player);
			Bukkit.broadcastMessage("HELLO");
			team.setPrefix(covePlayer.getServerRank().getNiceName(true));
			team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		}

	}

	private void setOld(Team team, Player player) {
		Bukkit.broadcastMessage("CRY");
		try {
			Object e = t.get(team);
			Object o1 = k.invoke(e, new Object[0]);
			if (player.hasPermission("stoppushing.allow")) {
				if (!o1.equals(ALLOW)) {
					a.invoke(e, new Object[]{ALLOW});
				}
			} else if (!o1.equals(NEVER)) {
				a.invoke(e, new Object[]{NEVER});
			}
		} catch (Exception var5) {
			var5.printStackTrace();
		}

	}

	private void set(Player player) {
		Scoreboard sb = player.getScoreboard();
		if (sb == null) {
			sb = Bukkit.getScoreboardManager().getNewScoreboard();
		}

		String name = player.getName();
		Team team = sb.getEntryTeam(name);
		if (team == null) {
			String n = name;

			while (sb.getTeam(n) != null) {
				n = n + all[r.nextInt(all.length - 1)];
				if (n.length() > 16) {
					n = n.substring(1);
				}
			}

			team = sb.registerNewTeam(n);
			team.addEntry(name);
		}

		if (!old) {
			this.set(team, player);
		} else {
			this.setOld(team, player);
		}

	}*/
}
