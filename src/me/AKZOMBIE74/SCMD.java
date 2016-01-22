package me.AKZOMBIE74;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class SCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;

        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("ss")) {


                try {
                    for (String key : Selector.getInstance().getSectionKeys()) {

                        Selector.getInstance().getOut().writeUTF("ConnectOther");
                        Selector.getInstance().getOut().writeUTF(p.getName());
                        Selector.getInstance().getOut().writeUTF(Selector.getInstance().getConfig().getString("Servers." + key + ".name"));
                        Thread.sleep(1000);
                        Bukkit.getServer().sendPluginMessage(Selector.getInstance(), "BungeeCord", Selector.getInstance().getB().toByteArray());
                        p.sendMessage(ChatColor.GREEN + "Successfully teleported to " + Selector.getInstance().getConfig().getString("Servers." + key + ".name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.print("Only players may use this command");
        }
        return false;
    }
}
