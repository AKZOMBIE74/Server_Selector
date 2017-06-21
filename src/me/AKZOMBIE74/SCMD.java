package me.AKZOMBIE74;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class SCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;

        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("ss")) {
                HashMap<UUID, String> selected = Selector.getInstance().getSelected();
                if (selected.containsKey(p.getUniqueId()) && selected.get(p.getUniqueId())!=null){
                    String name = selected.get(p.getUniqueId());
                    selected.put(p.getUniqueId(), null);
                    teleportServer(p, name);
                } else {
                    if (args.length>0){
                        teleportServer(p, args[0]);
                    } else {
                        help(p);
                    }
                }
            }
        } else {
            System.out.print("Only players may use this command");
        }
        return false;
    }

    public void teleportServer(Player p, String server){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException eee) {
            // Fehler
            eee.printStackTrace();
        }

        p.sendPluginMessage(Selector.getInstance(), "BungeeCord", b.toByteArray());
        if (serverExists(server)) {
            p.sendMessage(ChatColor.GREEN + "Successfully teleported to " + server);
        } else {
            p.sendMessage(ChatColor.RED+"Server not found");
        }

    }
    public void help(Player p){
        p.sendMessage(ChatColor.GOLD+"=====ServSel=====");
        p.sendMessage(ChatColor.GREEN+"By: AKZOMBIE74");
        p.sendMessage(ChatColor.AQUA+"Version: "+Selector.getInstance().getDescription().getVersion());
        p.sendMessage(ChatColor.BLUE+"/ss <server> - Teleports you to the specified server");
    }
    public boolean serverExists(String server){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("ServerIP");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Selector.getInstance().getServer().sendPluginMessage(Selector.getInstance(), "BungeeCord", b.toByteArray());
        return Selector.getPML().getServerip() != null;
    }
}
