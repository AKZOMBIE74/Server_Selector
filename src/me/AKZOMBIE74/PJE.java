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

        ItemMeta meta = PIE.x.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Selector.getInstance().getConfig().getString("Compass.name")));
        meta.setLore(Selector.getInstance().getConfig().getStringList("Compass.lore"));

        PIE.x.setItemMeta(meta);

        ItemStack z = new ItemStack(Material.AIR, 1);

        ItemMeta m = z.getItemMeta();
        m.setDisplayName("Air");

        if (!p.getInventory().contains(PIE.x)) {
            p.getInventory().addItem(PIE.x);
        }
    }
}
