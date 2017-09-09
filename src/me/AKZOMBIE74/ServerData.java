package me.AKZOMBIE74;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AKZOMBIE74 on 6/30/2017.
 */
public class ServerData {
    private Material material;//What material this server should display as
    private ItemMeta itemMeta;//The data that defines the material
    private int slot;//Slot # for the server to show up int GUI
    private boolean showcount, playerList; //showcount = # of players online, playerlist=names of players online
    private String name;//Official server name
    private List<String> defaultLore; //Lore from config

    ServerData(ItemMeta itemMeta, int slot, boolean showcount, boolean playerList, Material material, String name){
        this.itemMeta = itemMeta;
        defaultLore = this.itemMeta.getLore();
        this.slot = slot;
        this.showcount = showcount;
        this.playerList = playerList;
        this.material = material;
        this.name = name;
    }

    public void callShowCount(){
        if (showcount){
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            List<String> lore = itemMeta.getLore();
            try {
                out.writeUTF("PlayerCount");
                out.writeUTF(name);
                Bukkit.getServer().sendPluginMessage(Selector.getInstance(), "BungeeCord", b.toByteArray());
            } catch (IOException e){
                e.printStackTrace();
            }
            if (Selector.getInstance().getPlayerCounts().containsKey(name)
                    && Selector.getInstance().getPlayerCounts().get(name)!= null) {
                lore.add("Players Count: " + Selector.getInstance().getPlayerCounts().get(name));
            } else {
                lore.add("Players Count: 0");
            }
            itemMeta.setLore(lore);
        }
    }

    public void callPlayerList(){
        if (playerList){
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            List<String> lore = itemMeta.getLore();
            try {
                out.writeUTF("PlayerList");
                out.writeUTF(name);
                Bukkit.getServer().sendPluginMessage(Selector.getInstance(), "BungeeCord", b.toByteArray());
            } catch (IOException e){
                e.printStackTrace();
            }
            lore.add("Players Online: ");
            if (Selector.getInstance().getPlayerLists().containsKey(name)
                    && Selector.getInstance().getPlayerLists().get(name).length>0) {
                lore
                        .addAll(
                                Arrays.asList(Selector.getInstance().getPlayerLists().get(name)));
            } else {
                lore.add("None");
            }
            itemMeta.setLore(lore);
        }
    }

    public ItemStack getStack(){
        ItemStack is = new ItemStack(material, 1);
        List<String> finalLore = new ArrayList<>();
        itemMeta.getLore().forEach(l -> {
            finalLore.add(ChatColor.translateAlternateColorCodes('&', l));
        });
        itemMeta.setLore(finalLore);
        is.setItemMeta(itemMeta);
        itemMeta.setLore(defaultLore);
        return is;
    }

    public int getSlot() {
        return slot;
    }

    public String getName(){
        return name;
    }
}
