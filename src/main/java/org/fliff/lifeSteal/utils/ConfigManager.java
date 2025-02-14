package org.fliff.lifeSteal.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.fliff.lifeSteal.LifeSteal;

import java.io.File;

public class ConfigManager {

    private static FileConfiguration config;
    private static File configFile;

    public ConfigManager() {
        loadConfig();
    }

    public void loadConfig() {
        LifeSteal plugin = LifeSteal.getInstance();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        loadConfig(); // Reload the config
    }

    public String getHeartItemName() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("heart-item-name", "&c&lHeart"));
    }

    public int getMaxHealth() {
        return config.getInt("max-health", 20);
    }

    public int getMinHealth() {
        return config.getInt("min-health", 1);
    }

    public String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
