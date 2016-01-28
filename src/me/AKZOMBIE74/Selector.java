package me.AKZOMBIE74;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by AKZOMBIE74 on 11/11/2015.
 */
public class Selector extends JavaPlugin{
    private Set<String> sectionKeys;

    private ByteArrayOutputStream b = new ByteArrayOutputStream();
    private DataOutputStream out = new DataOutputStream(b);

    private static Selector instance;

    private static PML pml = new PML();


    //onEnable stuff
    @Override
    public void onEnable() {
        instance = this;

        //Register Commands
        getCommand("ss").setExecutor(new SCMD());

        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", getPML());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Register Events
        Bukkit.getServer().getPluginManager().registerEvents(new PIE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ICE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PJE(), this);

        //Create Config
        createConfig();

        //Enable Log
        getLogger().info("Server Selector has been enabled");
    }

    public static PML getPML() {
        return pml;
    }

    //onDisable stuff
    @Override
    public void onDisable() {
        instance = null;

        //Save Config
        saveConfig();
        //Disable Log
        getLogger().info("Server Selector has been disabled");
    }

    public ByteArrayOutputStream getB() {
        return b;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public static Selector getInstance() {
        return instance;
    }

    public Set<String> getSectionKeys() {
        return sectionKeys;
    }

    private void createConfig() {

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();



                String[] list = new String[]{"Go",
                        "To",
                        "this server!"
                };

                String[] list2 = {
                        "Right Click",
                        "To Open the",
                        "GUI"};

                getConfig().createSection("Servers");
                getConfig().set("slot_size", 9);
                getConfig().set("menu_name", "&bServer Selector");
                getConfig().set("Compass.name", "&bNameHere");
                getConfig().set("Compass.lore", list2);
                getConfig().set("Servers.server1.Material", "COMPASS");
                getConfig().set("Servers.server1.slot", 1);
                getConfig().set("Servers.server1.name", "server name here");
                getConfig().set("Servers.server1.display-name", "Minigames!");
                getConfig().set("Servers.server1.showcount", true);
                getConfig().set("Servers.server1.lore", list);
                saveConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public void OpenGui(Player player) throws IOException {
        Inventory inv = Bukkit.createInventory(null, getConfig().getInt("slot_size"), ChatColor.translateAlternateColorCodes('&', getConfig().getString("menu_name")));

        sectionKeys = getConfig().getConfigurationSection("Servers").getKeys(false);

        AtomicReference<ArrayList<ItemStack>> items = new AtomicReference<>(new ArrayList<ItemStack>(sectionKeys.size()));


        for (String key : sectionKeys) {
            if (sectionKeys != null) {
                String displayName = getConfig().getString("Servers." + key + ".display-name");
                String name = getConfig().getString("Servers." + key + ".name");
                final List<String> lore = getConfig().getStringList("Servers." + key + ".lore");
                Boolean showcount = getConfig().getBoolean("Servers." + key + ".showcount");
                Material m = Material.valueOf(getConfig().getString("Servers." + key + ".Material"));

                ItemStack stack = new ItemStack(m, 1);

                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                if (showcount) {

                    out.writeUTF("PlayerCount");
                    out.writeUTF(name);
                    Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
                    lore.add("Players Online: "+ getPML().getPc());
                }

                meta.setLore(lore);


                stack.setItemMeta(meta);

                items.get().add(stack);
                inv.setItem(getConfig().getInt("Servers." + key + ".slot"), stack);
            }
        }


        player.openInventory(inv);

    }


}
