package me.AKZOMBIE74;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
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

    private SCMD scmd;
    private File file;

    private IConfig lang;

    public String SERVER_NOT_FOUND;
    public String TELEPORTED;
    public String ONLY_PLAYERS;


    //onEnable stuff
    @Override
    public void onEnable() {
        instance = this;
        scmd = new SCMD();
        lang = new IConfig(instance, "lang.yml");

        if (!(getLang().isString("server-not-found-message")
                && getLang().isString("teleported-message")
                && getLang().isString("only-players-message"))) {
            writeExampleLang();
        }

        //Set String Variables
        SERVER_NOT_FOUND = getLang().getColored("server-not-found-message"); //Placeholders: %pn = player name, %s = server name, %pdn = player display name
        TELEPORTED = getLang().getColored("teleported-message");//Placeholders: %pn = player name, %s = server name, %pds = player display name
        ONLY_PLAYERS = getLang().getColored("only-players-message");

        //Register Commands
        getCommand("ss").setExecutor(scmd);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", getPML());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Register Events
        Bukkit.getServer().getPluginManager().registerEvents(new PIE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ICE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PJE(), this);

        //Create Config
        createConfig();


        //Enable Log
        getLogger().info("ServSel has been enabled");
    }

    public static PML getPML() {
        return pml;
    }

    //onDisable stuff
    @Override
    public void onDisable() {
        pml = null;
        b = null;
        out = null;
        if (sectionKeys!=null&&sectionKeys.size()>0) {
            sectionKeys.clear();
        }
        sectionKeys = null;
        scmd = null;
        file = null;
        instance = null;
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
            file = new File(getDataFolder(), "config.yml");
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
                getConfig().set("Servers.server1.showPlayerList", true);
                saveConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void writeExampleLang(){
        getLang().set("server-not-found-message", "&cServer, %s, not found for player %pdn");
        getLang().set("teleported-message", "&aSuccessfully teleported %pn to %s");
        getLang().set("only-players-message", "Only players may use this command");
        getLang().save();
    }


    public void OpenGui(Player player) throws IOException {
        Inventory inv = Bukkit.createInventory(null, getConfig().getInt("slot_size"), ChatColor.translateAlternateColorCodes('&', getConfig().getString("menu_name")));

        sectionKeys = getConfig().getConfigurationSection("Servers").getKeys(false);

        AtomicReference<ArrayList<ItemStack>> items = new AtomicReference<>(new ArrayList<ItemStack>(sectionKeys.size()));


        for (String key : sectionKeys) {
            if (sectionKeys != null) {
                String displayName = getConfig().getString("Servers." + key + ".display-name");
                String name = getConfig().getString("Servers." + key + ".name");
                List<String> lore = getConfig().getStringList("Servers." + key + ".lore");
                List<String> finalLore = new ArrayList<>();
                Boolean showcount = getConfig().getBoolean("Servers." + key + ".showcount");
                Boolean playerList = getConfig().getBoolean("Servers."+key+".showPlayerList");
                Material m = Material.valueOf(getConfig().getString("Servers." + key + ".Material"));

                ItemStack stack = new ItemStack(m, 1);

                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                if (showcount) {

                    out.writeUTF("PlayerCount");
                    out.writeUTF(name);
                    Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
                    lore.add("Players Count: "+ getPML().getPc());
                }
                if (playerList){
                    out.writeUTF("PlayerList");
                    out.writeUTF(name);
                    Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
                    lore.add("Players Online: ");
                    if (getPML().getPlayerList()!=null) {
                        lore
                                .addAll(
                                        Arrays.asList(getPML().getPlayerList()));
                    } else {
                        lore.add("None");
                    }
                }
                lore.forEach(l -> {
                    finalLore.add(ChatColor.translateAlternateColorCodes('&', l));
                });
                meta.setLore(finalLore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);

                stack.setItemMeta(meta);

                items.get().add(stack);
                inv.setItem(getConfig().getInt("Servers." + key + ".slot"), stack);
            }
        }


        player.openInventory(inv);

    }

    public IConfig getLang(){
        return lang;
    }
}
