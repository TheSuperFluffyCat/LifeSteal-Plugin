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

    public String getMessage(String path, String... replacements) {
        String raw = config.getString("messages." + path, "&cMessage missing: " + path);
        if (replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                raw = raw.replace(replacements[i], replacements[i + 1]);
            }
        }
        return formatMessage(raw);
    }

}
