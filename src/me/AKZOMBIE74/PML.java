package me.AKZOMBIE74;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Created by AKZOMBIE74 on 1/27/2016.
 */
public class PML implements PluginMessageListener{

    private int pc;

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("PlayerCount")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
            String server = in.readUTF(); // Name of server, as given in the arguments
            pc = in.readInt();
        }

    }

    public int getPc() {
        return pc;
    }
}
