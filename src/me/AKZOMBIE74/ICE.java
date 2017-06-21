package me.AKZOMBIE74;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class ICE implements Listener {

    @EventHandler
    public void clickI(InventoryClickEvent e) {

        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Selector.getInstance().getConfig().getString("menu_name")))) {

            Player p = (Player) e.getWhoClicked();


            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()) {
                p.closeInventory();
                return;
            }
            switch (e.getCurrentItem().getType()) {
                default:
                    for (String key : Selector.getInstance().getSectionKeys()) {
                        int sl = Selector.getInstance().getConfig().getInt("Servers."+key+".slot");
                        String name = Selector.getInstance().getConfig().getString("Servers."+key+".name");
                        if (e.getSlot()==sl) {
                            Selector.getInstance().getSelected().put(p.getUniqueId(), name);
                            break;
                        }
                    }
                    p.performCommand("ss");
                    p.closeInventory();
                    break;
            }
        }

    }
}
