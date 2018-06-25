package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

import json.JSONException;
import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Database.RedditDatabase;
import network.reborn.core.Module.Games.UltraHardcoreReddit.Menus.GameMenu;
import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.AbstractCommand;
import network.reborn.core.Util.GUI;
import network.reborn.core.Util.UUIDAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameDataMenu extends AbstractCommand {

    ArrayList<Player> ignore = new ArrayList<>();

	public GameDataMenu(String command, String usage, String description,
			List<String> aliases) {
		super(command, usage, description, aliases);
	}

	@SuppressWarnings("deprecation")
    public static void openInv(Player p, int page) {
        /*
		 * if (size < 9) { size = 9; } if (size % 9 != 0) {
		 * Bukkit.getLogger().info("Size != multiple of 9"); while (size % 9 !=
		 * 0) { size++; } }
		 */
        RedditDatabase.messagePlayer(p, ChatColor.YELLOW
                + "Loading Game Archive, please wait...");
        Bukkit.getScheduler().scheduleAsyncDelayedTask(RebornCore.getRebornCore(),
                new Runnable() {
					@Override
					public void run() {
                        ArrayList<GameData> data = (ArrayList<GameData>) ((UltraHardcoreReddit) RebornCore.getCoveAPI().getGame()).getDBManager().getCurrentData();
                        if (data.isEmpty()) {
                            RedditDatabase.messagePlayer(p, ChatColor.RED
                                    + "The Archive isn't loaded yet. Please try again in a bit.");
							return;
						}
                        Collections.sort(data,
                                (left, right) -> left.getID() - right.getID());
                        HashMap<GameData, String> names = new HashMap<GameData, String>();
                        for (GameData d : data) {
                            String name = "";
								try {
									name = UUIDAPI.getNameMCAPI(d.getUUID());
								} catch (JSONException e) {
									name = "ERROR";
									e.printStackTrace();
								}
								names.put(d, name);
							}
                        next(p, page, names, data);

					}

				});
	}

    static void next(Player p, int page,
                     HashMap<GameData, String> da, ArrayList<GameData> data) {
        try {
            GUI gui = new GUI("Game Archive");
            List<GameData> list = data;

			for (GameData d : list) {
				String name = da.get(d);
				String dt = d.getDateTime();
				String s = d.getScenarios();
                String url = d.getMatchPostURL();
                ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1,
						(short) 3);
				if (name.equals("ERROR")) {
					playerhead = new ItemStack(Material.BARRIER);
					ItemMeta hm = playerhead.getItemMeta();
					hm.setDisplayName(ChatColor.DARK_RED + "ERROR");
					ArrayList<String> l = new ArrayList<String>();
					l.add(ChatColor.RED + "Error while converting");
					l.add(ChatColor.RED + "UUID to Username.");
					l.add(ChatColor.RED + "");
					l.add(ChatColor.RED + "Maybe the UUID API website");
					l.add(ChatColor.RED + "is offline? (" + ChatColor.AQUA
                            + "https://sessionserver.mojang.com" + ChatColor.RED + ")");
                    l.add(ChatColor.RED + "");
					l.add(ChatColor.YELLOW + "#" + String.valueOf(d.getID()));
					l.add(ChatColor.YELLOW + "UUID: " + d.getUUID());

					hm.setLore(l);
					playerhead.setAmount(1);
					playerhead.setItemMeta(hm);
				} else {
					SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
					meta.setOwner(name);
					meta.setDisplayName(ChatColor.YELLOW + "Game #"
							+ String.valueOf(d.getID()));
					ArrayList<String> l = new ArrayList<String>();
					l.add(ChatColor.AQUA + "Host: " + ChatColor.GREEN + name);
					l.add(ChatColor.AQUA + "Hosted: " + ChatColor.GREEN + dt);
					l.add(ChatColor.AQUA + "Scenarios: " + ChatColor.GREEN + s);
                    l.add(ChatColor.AQUA + "Link: " + ChatColor.GREEN + url);
                    meta.setLore(l);
					playerhead.setItemMeta(meta);
				}
				playerhead.setAmount(1);
                gui.addItem(playerhead);
            }

			p.sendMessage(ChatColor.GREEN
					+ "Game Archive downloaded from database.");
            p.openInventory(gui.create(1));
        } catch (Exception e) {
			Bukkit.getLogger()
					.info("Error was called on GameDataMenu line 206");
			e.printStackTrace();
		}

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            RebornPlayer cp = RebornCore.getCoveAPI().getCovePlayer(p.getUniqueId());
            if (!cp.canPlayer(ServerRank.MODERATOR)) {
                p.sendMessage(ChatColor.RED
                        + "No access to Game Archive.");
                return true;
            }
            if (!RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.WAITING)) {
                p.sendMessage(ChatColor.RED
                        + "The Archive cannot be accessed during a game.");
                return true;
            }
            openInv(p, 1);
        }
        return true;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (ignore.contains(p)) {
            ignore.remove(p);
            return;
        }
        if (event.getView().getTopInventory() != null) {
            if (event.getView().getTopInventory().getTitle() != null) {
                if (event.getView().getTopInventory().getTitle().contains("Game Archive")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), new Runnable() {
                        @Override
                        public void run() {
                            GameMenu.openMenu(p);
                        }
                    }, 5L);

                }
            }
        }
    }

}
