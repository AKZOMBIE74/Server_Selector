package me.AKZOMBIE74;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AKZOMBIE74 on 9/9/2017.
 */
public class ServerTabCompleter implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> serverNames = new ArrayList<>();
        Selector.getInstance().getServerData().forEach(server -> serverNames.add(server.getName()));
        return serverNames.size()>0? serverNames : null;
    }
}
