package network.reborn.core.Util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.github.paperspigot.Title;

public class Titles implements PluginMessageListener {
    public static void sendTitle(Player p, Integer fadeIn, Integer stay,
                                 Integer fadeOut, String title, String subtitle) {
        // TODO: Bukkit Code
        /*
		 * PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
		 *
		 * PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(
		 * EnumTitleAction.TIMES, null, fadeIn.intValue(), stay.intValue(),
		 * fadeOut.intValue()); c.sendPacket(packetPlayOutTimes); if (subtitle
		 * != null) { subtitle = ChatColor.translateAlternateColorCodes('&',
		 * subtitle); IChatBaseComponent titleSubtitle = ChatSerializer .a(
		 * "{\"text\": \"" + subtitle + "\"}"); PacketPlayOutTitle
		 * packetPlayOutSubTitle = new PacketPlayOutTitle(
		 * EnumTitleAction.SUBTITLE, titleSubtitle);
		 * c.sendPacket(packetPlayOutSubTitle); } if (title != null) { title =
		 * ChatColor.translateAlternateColorCodes('&', title);
		 * IChatBaseComponent titleMain = ChatSerializer .a("{\"text\": \"" +
		 * title + "\"}"); PacketPlayOutTitle packetPlayOutTitle = new
		 * PacketPlayOutTitle( EnumTitleAction.TITLE, titleMain);
		 * c.sendPacket(packetPlayOutTitle); }
		 */
        // TODO: PaperSpigot Code
        p.sendTitle(
                new Title(ChatColor.translateAlternateColorCodes('&', title),
                        ChatColor.translateAlternateColorCodes('&', subtitle),
                        fadeIn, stay, fadeOut));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
        Player player = null;
        String title = "";
        String subtitle = "";
        int fadeIn = 0;
        int stay = 0;
        int fadeOut = 0;
        if (!channel.equals("NGC-TITLE") || !channel.equals("NGC-TS-PLAYER")
                || !channel.equals("NGC-SUBTITLE")
                || !channel.equals("NGC-TS-FADEIN")
                || !channel.equals("NGC-TS-STAY")
                || !channel.equals("NGC-TS-FADEOUT")) {
            return;
        }

        if (channel.equals("NGC-TS-PLAYER")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            player = Bukkit.getPlayer(input);
        }
        if (channel.equals("NGC-TITLE")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            title = input;
        }
        if (channel.equals("NGC-SUBTITLE")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            subtitle = input;
        }
        if (channel.equals("NGC-TS-FADEIN")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            fadeIn = Integer.valueOf(input);
        }
        if (channel.equals("NGC-TS-STAY")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            stay = Integer.valueOf(input);
        }
        if (channel.equals("NGC-TS-FADEOUT")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(msg);
            String input = in.readUTF();
            fadeOut = Integer.valueOf(input);
        }
        sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);

    }
}
