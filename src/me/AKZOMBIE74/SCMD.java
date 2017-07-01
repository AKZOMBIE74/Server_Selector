package me.AKZOMBIE74;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class SCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if (cmd.getName().equalsIgnoreCase("ss")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length>0){
                    teleportServer(p, args[0]);
                } else {
                    help(p);
                    updateMessage(p);
                }
            } else {
                sender.sendMessage(Selector.getInstance().ONLY_PLAYERS);
            }
        } else if (cmd.getName().equalsIgnoreCase("ssr")){
            Selector.getInstance().checkForServers();
            Selector.getInstance().setLangVars();
            Selector.getInstance().checkForUpdates();
            updateMessage(sender);
            sender.sendMessage(ChatColor.GREEN+"Successfully reloaded ServSel!");
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
            p.sendMessage(Selector.getInstance().TELEPORTED.replaceAll("%pn", p.getName()).replaceAll("%s", server)
                    .replaceAll("%pdn", p.getDisplayName()));
        } else {
            p.sendMessage(Selector.getInstance().SERVER_NOT_FOUND.replaceAll("%pn", p.getName()).replaceAll("%s", server)
                    .replaceAll("%pdn", p.getDisplayName()));
        }

    }
    public void help(Player p){
        p.sendMessage(ChatColor.GOLD+"=====ServSel=====");
        p.sendMessage(ChatColor.GREEN+"By: AKZOMBIE74");
        p.sendMessage(ChatColor.AQUA+"Version: "+Selector.getInstance().CURRENT_VERSION);
        p.sendMessage(ChatColor.BLUE+"/ss <server> - Teleports you to the specified server");
        p.sendMessage(ChatColor.BLUE+"/ssr - Reloads ServSel");
        Selector.getInstance().checkForUpdates();
    }
    public void updateMessage(CommandSender p) {
        if (Selector.getInstance().shouldUpdate){
            p.sendMessage(ChatColor.DARK_RED+"[ServSel] It is recommended you update to version "+Selector.getInstance().VERSION);
            p.sendMessage(ChatColor.YELLOW+Selector.getInstance().CHANGELOG);
        }
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
