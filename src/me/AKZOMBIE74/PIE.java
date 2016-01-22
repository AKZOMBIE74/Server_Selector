package me.AKZOMBIE74;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * Created by AKZOMBIE74 on 1/22/2016.
 */
public class PIE implements Listener {
    public static ItemStack x;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) throws IOException {
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().equals(x)) {
                Selector.getInstance().OpenGui(p);
            }
        }
    }
}
