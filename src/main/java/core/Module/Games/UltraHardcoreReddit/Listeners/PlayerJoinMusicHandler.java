package network.reborn.core.Module.Games.UltraHardcoreReddit.Listeners;

import com.xxmicloxx.NoteBlockAPI.*;
import network.reborn.core.Module.Games.GameState;
import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;

/**
 * Created by ethan on 12/19/2016.
 */
public class PlayerJoinMusicHandler implements Listener {

    public static HashMap<String, SongPlayer> players = new HashMap<String, SongPlayer>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (RebornCore.getCoveAPI().getGame().getGameTitle().contains("Reddit")) {
            if (RebornCore.getCoveAPI().getGame().getGameState().equals(GameState.WAITING)) {
                Song s = NBSDecoder.parse(new File("plugins" + File.separator + "NoteBlockPlayer" + File.separator + "getready.nbs"));
                SongPlayer sp = new RadioSongPlayer(s);
                sp.setAutoDestroy(true);
                sp.addPlayer(event.getPlayer());
                sp.setPlaying(true);
                players.put(event.getPlayer().getName(), sp);
            }
        }
    }

    @EventHandler
    public void onSongEnd(SongEndEvent event) {
        SongPlayer sp = new RadioSongPlayer(event.getSongPlayer().getSong());
        if (Bukkit.getPlayer(event.getSongPlayer().getPlayerList().get(0)) != null) {
            Player p = Bukkit.getPlayer(event.getSongPlayer().getPlayerList().get(0));
            if (p.isOnline()) {
                sp.setAutoDestroy(true);
                sp.addPlayer(p);
                sp.setPlaying(true);
                players.remove(p.getName());
                players.put(p.getName(), sp);
            }
            event.getSongPlayer().destroy();
        }

    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        players.get(event.getPlayer().getName()).destroy();
        players.remove(event.getPlayer().getName());
    }



}
