package network.reborn.core.Module.Games.UltraHardcoreReddit.Pregame.Parkour;

import network.reborn.core.Module.Games.UltraHardcoreReddit.UltraHardcoreReddit;
import network.reborn.core.RebornCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ethan on 12/20/2016.
 */
public class ParkourSystem implements Listener {

    HashMap<UUID, Long> startTime = new HashMap<>();
    HashMap<UUID, ParkourTiers> level = new HashMap<>();
    HashMap<UUID, Long> total = new HashMap<>();
    HashMap<UUID, Location> respawn = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (event.getClickedBlock() != null) {
                Block b = event.getClickedBlock();
                Location bl = b.getLocation();
                if (!bl.getWorld().getName().equalsIgnoreCase("map-uhc")) {
                    return;
                }
                if (bl.getWorld().getBlockAt(bl.getBlockX(), bl.getBlockY() - 1, bl.getBlockZ()) != null) {
                    Block u = bl.getWorld().getBlockAt(bl.getBlockX(), bl.getBlockY() - 1, bl.getBlockZ());
                    if (u.getType().equals(Material.WOOL)) {
                        ParkourTiers t = getTierFromBlock(u);
                        if (t == null)
                            return;
                        //START
                        if (b.getType().equals(Material.IRON_PLATE)) {
                            UUID id = event.getPlayer().getUniqueId();
                            if (level.containsKey(id)) {
                                if (level.get(id).equals(t)) {
                                    return;
                                }
                                level.remove(id);
                                level.put(id, t);
                                respawn.remove(id);
                                respawn.put(id, event.getPlayer().getLocation());
                                if (t.equals(ParkourTiers.EXTREME)) {
                                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Now on " + ChatColor.DARK_RED + "E" + ChatColor.BLACK + "" + ChatColor.MAGIC + "XT" + ChatColor.RESET + "" + ChatColor.DARK_RED + "R" + ChatColor.BLACK + "" + ChatColor.MAGIC + "EME" + ChatColor.RESET + "" + ChatColor.YELLOW + " Parkour");
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Now on " + t.getName() + " Parkour.");
                                }
                                return;
                            }
                            startTime.put(id, System.currentTimeMillis());
                            level.put(id, t);
                            respawn.put(id, event.getPlayer().getLocation());
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "You are now in Parkour Mode.");
                            if (t.equals(ParkourTiers.EXTREME)) {
                                event.getPlayer().sendMessage(ChatColor.YELLOW + "Now on " + ChatColor.DARK_RED + "E" + ChatColor.BLACK + "" + ChatColor.MAGIC + "XT" + ChatColor.RESET + "" + ChatColor.DARK_RED + "R" + ChatColor.BLACK + "" + ChatColor.MAGIC + "EME" + ChatColor.RESET + "" + ChatColor.YELLOW + " Parkour");
                            } else {
                                event.getPlayer().sendMessage(ChatColor.YELLOW + "Now on " + t.getName() + " Parkour.");
                            }


                        }
                        //FINISH
                        else if (b.getType().equals(Material.GOLD_PLATE)) {
                            UUID id = event.getPlayer().getUniqueId();
                            if (!level.containsKey(id)) {
                                event.getPlayer().sendMessage(ChatColor.RED + "You are not in Parkour Mode! Step on an Iron Pressure Plate to begin!");
                                return;
                            }
                            Long time = 0L;
                            if (total.containsKey(id)) {
                                time += total.get(id);
                            }
                            Long d = System.currentTimeMillis() - startTime.get(id);
                            time += d;
                            total.remove(id);
                            level.remove(id);
                            startTime.remove(id);
                            respawn.remove(id);
                            long second = (time / 1000) % 60;
                            long minute = (time / (1000 * 60)) % 60;
                            long hour = (time / (1000 * 60 * 60)) % 24;
                            String display = String.format("%02d:%02d:%02d", hour, minute, second);
                            event.getPlayer().teleport(bl.getWorld().getSpawnLocation());
                            if (t.equals(ParkourTiers.EASY)) {
                                UltraHardcoreReddit.sendUHCMessage(ChatColor.YELLOW + "Congratulations, " + ChatColor.GREEN + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " on completing the " + t.getName() + " Parkour! Try to complete a higher difficulty to get recognized in the chat!", event.getPlayer());
                            } else {
                                if (t.equals(ParkourTiers.EXTREME)) {
                                    event.getPlayer().getWorld().playSound(bl.getWorld().getSpawnLocation(), Sound.ENDERDRAGON_GROWL, 10, 0);
                                    UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GOLD + "Congratulations to " + ChatColor.GREEN + event.getPlayer().getDisplayName() + ChatColor.GOLD + " for fully completing the Pregame Parkour on " + ChatColor.DARK_RED + "Extreme" + ChatColor.GOLD + " Difficulty! They beat the parkour in " + ChatColor.AQUA + display);
                                } else {
                                    UltraHardcoreReddit.broadcastUHCMessage(ChatColor.GREEN + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " completed the " + t.getName() + " Parkour in " + ChatColor.AQUA + display);
                                }
                            }
                        }
                    } else if (u.getType().equals(Material.DIAMOND_BLOCK)) {
                        event.getPlayer().teleport(new Location(u.getWorld(), 242.5, 83.5, 1359.5, (float) 0, (float) 0));
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5, 1));
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERMAN_SCREAM, 5, 0);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockY() < 26 && event.getTo().getWorld().getName().equals("map-uhc")) {
            if (respawn.containsKey(event.getPlayer().getUniqueId())) {
                event.getPlayer().teleport(respawn.get(event.getPlayer().getUniqueId()));
            } else {
                event.getPlayer().teleport(RebornCore.getCoveAPI().getGame().getGameSettings().getGameLobby());
            }

        }
    }

    ParkourTiers getTierFromBlock(Block b) {
        byte d = b.getData();
        //EASY
        if (d == (byte) 5) {
            return ParkourTiers.EASY;
        }
        //MEDIUM
        else if (d == (byte) 1) {
            return ParkourTiers.MEDIUM;
        }
        //HARD
        else if (d == (byte) 14) {
            return ParkourTiers.HARD;
        } else if (d == (byte) 15) {
            return ParkourTiers.EXTREME;
        }
        return null;

    }


}
