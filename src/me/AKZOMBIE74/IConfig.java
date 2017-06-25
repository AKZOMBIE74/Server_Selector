package me.AKZOMBIE74;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

public class IConfig extends YamlConfiguration {

    private final Plugin plugin;

    private final String fileName;

    private final File file;

    private final String subFolder;

    public IConfig(Plugin plugin, File file) {
        this.plugin = plugin;
        this.fileName = file.getName();
        this.file = file;
        this.subFolder = null;

        create();
    }

    public IConfig(Plugin plugin, String fileName) {
        this(plugin, fileName, null);
    }

    public IConfig(Plugin plugin, String fileName, String subFolder) {
        this.plugin = plugin;
        this.fileName = (fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        this.subFolder = subFolder;
        this.file = new File(this.plugin.getDataFolder(), getPath());

        create();
    }

    private String getPath() {
        return (this.subFolder == null ? this.fileName : this.subFolder + "/" + this.fileName);
    }

    public void save() {
        try {
            save(this.file);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error saving config file " + this.fileName + "!", e);
        }
    }

    public void reload() {
        create();
    }

    private void create() {
        try {
            InputStreamReader inputStreamReader = null;

            if (this.file.exists()) {
                inputStreamReader = new InputStreamReader(new BufferedInputStream(new FileInputStream(this.file)), StandardCharsets.UTF_8);
            } else {
                InputStream inputStream;

                if ((inputStream = this.plugin.getResource(getPath())) != null) {
                    inputStreamReader = new InputStreamReader(new BufferedInputStream(inputStream), StandardCharsets.UTF_8);
                }
            }

            if (inputStreamReader != null) {
                load(inputStreamReader);
            }
        } catch (IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error creating config file " + this.fileName + "!", e);
        }
    }

    public boolean exists() {
        return this.file.exists();
    }

    public boolean delete() {
        return exists() && this.file.delete();
    }

    public String getColored(String path) {
        String text = getString(path);
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    }

    public void setLocation(String path, Location location) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null) section = createSection(path);

        setLocation(section, location);
    }

    public void setLocation(ConfigurationSection section, Location location) {
        if (section == null) return;

        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public Location getLocation(String path) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null) return null;

        return getLocation(section);
    }

    public Location getLocation(ConfigurationSection section) {
        if (section == null) return null;

        return new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"));
    }

    public Map<String, Object> getMap(String path) {
        Object object = get(path);

        if(object instanceof MemorySection) {
            return ((MemorySection) object).getValues(false);
        } else if(object instanceof Map) {
            return ((Map<String, Object>) object);
        }

        return null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public String getSubFolder() {
        return subFolder;
    }
}

