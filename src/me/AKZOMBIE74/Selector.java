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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by AKZOMBIE74 on 11/11/2015.
 */
public class Selector extends JavaPlugin{
    private Set<String> sectionKeys;

    private ByteArrayOutputStream b = new ByteArrayOutputStream();
    private DataOutputStream out = new DataOutputStream(b);

    private HashMap<String, Integer> playerCounts;
    private HashMap<String, String[]> playerLists;
    private HashMap<String, String> serverExists;

    private static Selector instance;

    private static PML pml = new PML();

    private SCMD scmd;
    private File file;

    private IConfig lang;

    private ArrayList<ServerData> serverData;

    String SERVER_NOT_FOUND, TELEPORTED, ONLY_PLAYERS, VERSION,
    CURRENT_VERSION, CHANGELOG;

    Boolean shouldUpdate = false;


    //onEnable stuff
    @Override
    public void onEnable() {
        instance = this;
        scmd = new SCMD();
        playerCounts = new HashMap<>();
        playerLists = new HashMap<>();
        serverExists = new HashMap<>();

        //Make arraylist to store all servers
        serverData = new ArrayList<>();

        //Create Files
        createConfig();
        getLogger().info("Loading lang.yml");
        lang = new IConfig(instance, "lang.yml");

        if (!(getLang().isString("server-not-found-message")
                && getLang().isString("teleported-message")
                && getLang().isString("only-players-message")
                && getLang().isBoolean("show-update-message"))) {
            writeExampleLang();
        }
        getLogger().info("Finished loading lang.yml");

        //Register Commands
        getCommand("ss").setExecutor(scmd);
        getCommand("ssr").setExecutor(scmd);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", getPML());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Register Events
        Bukkit.getServer().getPluginManager().registerEvents(new PIE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ICE(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PJE(), this);


        //Set String Variables
        setLangVars();
        CURRENT_VERSION = getInstance().getDescription().getVersion();

        //Fill serverData
        checkForServers();

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
        playerCounts.clear();
        playerCounts = null;
        playerLists.clear();
        playerLists = null;
        serverExists.clear();
        serverExists = null;
        scmd = null;
        file = null;
        serverData.clear();
        serverData = null;
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
        if (!getLang().isString("server-not-found-message")) {
            getLang().set("server-not-found-message", "&cServer, %s, not found for player %pdn");
        }
        if (!getLang().isString("teleported-message")) {
            getLang().set("teleported-message", "&aSuccessfully teleported %pn to %s");
        }
        if (!getLang().isString("only-players-message")) {
            getLang().set("only-players-message", "Only players may use this command");
        }
        if (!getLang().isBoolean("show-update-message")) {
            getLang().set("show-update-message", true);
        }
        getLang().save();
    }


    public void OpenGui(Player player) throws IOException {
        Inventory inv = Bukkit.createInventory(null, getConfig().getInt("slot_size"), ChatColor.translateAlternateColorCodes('&', getConfig().getString("menu_name")));
        if (serverData != null && serverData.size()>0) {
            for (ServerData server : serverData) {
                server.callShowCount();
                server.callPlayerList();
                inv.setItem(server.getSlot(), server.getStack());
            }
        }
        player.openInventory(inv);
    }

    public IConfig getLang(){
        return lang;
    }

    private String connectToVersion(String server){
        URL uri;
        try {
            uri = new URL(server);
            URLConnection ec = uri.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    ec.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);

            in.close();
            return a.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     *         The result is a positive integer if str1 is _numerically_ greater than str2.
     *         The result is zero if the strings are _numerically_ equal.
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    public void checkForUpdates(){
        if (getLang().getBoolean("show-update-message")) {
            String VersionAndChangelog = connectToVersion(
                    "https://private-8f513b-myspigotpluginupdates.apiary-mock.com/questions").split(",")[0]
                    .replaceAll("\"", ""); //ServSel:Version:Changelog
            VERSION = VersionAndChangelog.split(":")[1]
                    .replaceAll(" ", "");//Version
            CHANGELOG = VersionAndChangelog.split(":")[2];//Changelog
            //Set boolean variable
            shouldUpdate = versionCompare(CURRENT_VERSION, VERSION) < 0;
        }
    }

    public void setLangVars(){
        getLang().reload();
        SERVER_NOT_FOUND = getLang().getColored("server-not-found-message"); //Placeholders: %pn = player name, %s = server name, %pdn = player display name
        TELEPORTED = getLang().getColored("teleported-message");//Placeholders: %pn = player name, %s = server name, %pds = player display name
        ONLY_PLAYERS = getLang().getColored("only-players-message");
    }

    public void checkForServers(){
        serverData.clear();
        reloadConfig();
        sectionKeys = getConfig().getConfigurationSection("Servers").getKeys(false);

        for (String key : sectionKeys) {
            if (sectionKeys != null) {
                String displayName = getConfig().getString("Servers." + key + ".display-name");
                String name = getConfig().getString("Servers." + key + ".name");
                List<String> lore = getConfig().getStringList("Servers." + key + ".lore");
                boolean showcount = getConfig().getBoolean("Servers." + key + ".showcount");
                boolean playerList = getConfig().getBoolean("Servers."+key+".showPlayerList");
                Material m = Material.valueOf(getConfig().getString("Servers." + key + ".Material"));

                ItemStack stack = new ItemStack(m, 1);

                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);

                serverData.add(new ServerData(meta, getConfig().getInt("Servers." + key + ".slot"),
                        showcount, playerList, m, name));
                serverExists.put(name, "1");
            }
        }
    }
    public ArrayList<ServerData> getServerData(){
        return serverData;
    }

    public HashMap<String, Integer> getPlayerCounts(){
        return playerCounts;
    }

    public HashMap<String, String[]> getPlayerLists(){
        return playerLists;
    }

    public HashMap<String, String> getServerExists(){
        return serverExists;
    }
}
