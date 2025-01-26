package org.fliff.lifeSteal.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.fliff.lifeSteal.LifeSteal;

public class ConfigManager {

    private final FileConfiguration config;

    public ConfigManager() {
        this.config = LifeSteal.getInstance().getConfig();
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
