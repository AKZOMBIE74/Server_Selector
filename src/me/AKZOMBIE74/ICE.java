package me.AKZOMBIE74;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class ICE implements Listener {

    @EventHandler
    public void clickI(InventoryClickEvent e) {

        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Selector.getInstance().getConfig().getString("menu_name")))) {
            String name = null;
            Player p = (Player) e.getWhoClicked();


            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()) {
                p.closeInventory();
                return;
            }
            if (e.getClick().isShiftClick()){
                e.setCancelled(true);
                return;
            }
            switch (e.getCurrentItem().getType()) {
                default:
                    for (String key : Selector.getInstance().getSectionKeys()) {
                        int sl = Selector.getInstance().getConfig().getInt("Servers."+key+".slot");
                        if (e.getSlot()==sl) {
                            name = Selector.getInstance().getConfig().getString("Servers."+key+".name");
                            break;
                        }
                    }
                    p.performCommand("ss "+name);
                    p.closeInventory();
                    break;
            }
        }

    }
}
