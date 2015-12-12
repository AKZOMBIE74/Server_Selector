package me.AKZOMBIE74;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by AKZOMBIE74 on 11/11/2015.
 */
public class Selector extends JavaPlugin implements PluginMessageListener, Listener {
    private Inventory inv;
    private Set<String> sectionKeys;
    private ItemStack stack;
    private ItemStack x;



    //onEnable stuff
    @Override
    public void onEnable() {

        //Register Commands
        getCommand("ss").setExecutor(this);

        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Register Events
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        //Create Config
        createConfig();

        //Enable Log
        getLogger().info("Compass Nav has been enabled");
    }

    //onDisable stuff
    @Override
    public void onDisable() {

        //Save Config
        saveConfig();
        //Disable Log
        getLogger().info("Server Selector has been disabled");
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
                //String[] list = {"BREAD", "DIAMOND_ORE", "GOLD_BLOCK"};
                String[] list = {"Go", "To", "this server!"};
                String[] list2 = {"Right Click", "To Open the", "GUI"};
                getConfig().createSection("Servers");
                getConfig().set("slot_size", 9);
                getConfig().set("menu_name", "&bServer Selector");
                getConfig().set("Compass.name", "&bNameHere");
                getConfig().set("Compass.lore", list2);
                getConfig().set("Servers.server1.Material", "COMPASS");
                getConfig().set("Servers.server1.slot", 1);
                getConfig().set("Servers.server1.name", "server name here");
                getConfig().set("Servers.server1.display-name", "Minigames!");
                getConfig().set("Servers.server1.lore", list);
                saveConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        /*try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String command = in.readUTF();

            if (command.equals("Connect")) {
                String[] serverList = in.readUTF().split(", ");
                player.sendMessage("Servers: " + serverList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } */
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
         x = new ItemStack(Material.COMPASS, 1);

        // Get the item meta, put your data on it
        ItemMeta meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Compass.name")));
        meta.setLore(getConfig().getStringList("Compass.lore"));

        //give back the modified meta to the ItemStack
        x.setItemMeta(meta);

        ItemStack z = new ItemStack(Material.AIR, 1);

        ItemMeta m = z.getItemMeta();
        m.setDisplayName("Air");

         if (!p.getInventory().contains(x)) {
            p.getInventory().addItem(x);
        }

       // if (p.getItemInHand().equals(x)) {
       //     p.getItemInHand().setItemMeta(z.getItemMeta());
        //}
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.getItemInHand().equals(x)) {
                OpenGui(p);
            }
        }
    }

    private void OpenGui(Player player) {
        inv = Bukkit.createInventory(null, getConfig().getInt("slot_size"), ChatColor.translateAlternateColorCodes('&', getConfig().getString("menu_name")));

       /* ItemStack survival = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta survivalmeta = survival.getItemMeta();

        survivalmeta.setDisplayName(ChatColor.GREEN+ "Random Teleport ");
        survival.setItemMeta(survivalmeta);

        inv.setItem(1, survival);

        ItemStack skyblock = new ItemStack(Material.GRASS );
        ItemMeta skyblockmeta = skyblock.getItemMeta();

        skyblockmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "SkyBlock");
        skyblock.setItemMeta(skyblockmeta);

        inv.setItem(7, skyblock); */

        //Based of the structure of your yml file... && Assuming that you don't expect an empty file...

// First, get the list of all keys representing each item section (getKey(false) will take only mother keys, and skip childs)
        sectionKeys = getConfig().getConfigurationSection("Servers").getKeys(false);

// Create a list to store all the items,
        List<ItemStack> items = new ArrayList<ItemStack>(sectionKeys.size());

        /*
        getConfig().set("items.NavI.Material", "COMPASS");
                getConfig().set("items.NavI.Loc.x", 3.0);
                getConfig().set("items.NavI.Loc.y", 3.0);
                getConfig().set("items.NavI.Loc.z", 3.0);
                getConfig().set("items.NavI.Loc.world", "homestead");
                getConfig().set("items.NavI.slot", 1);
                getConfig().set("items.NavI.name", "&bFirst Item");
        */

//Start a loop (check if sectionKeys is empty would be a plus...)
        for ( String key : sectionKeys )
        {
            // Get needed data under each dynamic path  ( with dots ! )
            String displayName = getConfig().getString("Servers." + key + ".display-name");
            List<String> lore = getConfig().getStringList("Servers." + key + ".lore");
            Material m = Material.valueOf(getConfig().getString("Servers." + key + ".Material"));

            // Build an ItemStack.. The Material is not specified in your file so... I put one by default, but do what you need
            stack = new ItemStack(m , 1);

            // Get the item meta, put your data on it
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setLore(lore);

            //give back the modified meta to the ItemStack
            stack.setItemMeta(meta);

            //Add this stack to the list..
            items.add(stack);
            inv.setItem(getConfig().getInt("Servers." + key + ".slot"), stack);
        }

// At this point, you have a list of ItemSTack ready to be used.


        player.openInventory(inv);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        Player p = (Player) sender;
       /* String bruh = RANDOM.nextBoolean() ? "Drugs": "UltraGames";
        String bruhs = RANDOM.nextBoolean() ? bruh: "OPFactions";
        String bruhsd = RANDOM.nextBoolean() ? bruhs: "Skyblock";
        String bruhss = RANDOM.nextBoolean() ? bruhsd: "Hub";
        String bruhsss = RANDOM.nextBoolean() ? bruhss: "KitPvP";
        String bruhsssss = RANDOM.nextBoolean() ? bruhsss: "Secret"; */
        //List<String> list = getConfig().getStringList("servers");
        //String item = list.get(RANDOM.nextInt(list.size()));

        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("ss")) {


                try {
                    for (String key : sectionKeys) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        out.writeUTF("ConnectOther");
                        out.writeUTF(p.getName());
                        out.writeUTF(getConfig().getString("Servers." +key+".name"));
                        Thread.sleep(1000);
                        Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
                        p.sendMessage(ChatColor.GREEN + "Successfully teleported to " + getConfig().getString("Servers." + key+".name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.print("Only players may use this command");
        }
        return false;
    }

    @EventHandler
    public void clickI(InventoryClickEvent e) {

       /* for (String TempString : this.getConfig().getConfigurationSection("thing1").getKeys(false)) {
            int x = getConfig().getInt("thing1." + TempString + ".material");
            int y = getConfig().getInt("thing1." + TempString + ".y");
            int z = getConfig().getInt("thing1." + TempString + ".z");
        } */
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("menu_name")))) {

            Player p = (Player) e.getWhoClicked();


            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()) {
                p.closeInventory();
                return;
            }
                switch (e.getCurrentItem().getType()) {
                    default:
                        p.performCommand("ss");
                        p.closeInventory();
                        break;
                }
        }

    }
}
