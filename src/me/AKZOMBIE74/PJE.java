package me.AKZOMBIE74;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class PJE implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PIE.x = new ItemStack(Material.COMPASS, 1);
        Boolean hasCompass = false;

        String name = ChatColor.translateAlternateColorCodes(
                '&', Selector.getInstance().getConfig().getString("Compass.name"));

        ItemMeta meta = PIE.x.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Selector.getInstance().getConfig().getStringList("Compass.lore"));

        PIE.x.setItemMeta(meta);

        for (ItemStack item : p.getInventory().getContents()) {
            if (item!=null)
                if (item.hasItemMeta())
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(name) && item.getType() == PIE.x.getType())
                        hasCompass = true;
        }
        if (!hasCompass) {
            p.getInventory().addItem(PIE.x);
        }
    }
}
