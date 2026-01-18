package de.survivalnight.luna.lifeSteal.utils;

import de.survivalnight.luna.lifeSteal.LifeSteal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final LifeSteal plugin;
    private final MiniMessage mini = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacy =
            LegacyComponentSerializer.builder()
                    .character('§')
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    public ConfigManager() {
        this.plugin = LifeSteal.getInstance();
        plugin.saveDefaultConfig();
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }



    public String getHeartItemName() {
        return formatMessage(
                cfg().getString("heart-item-name", "&c&lHeart")
        );
    }

    public int getMaxHealth() {
        return cfg().getInt("max-health", 20);
    }

    public int getMinHealth() {
        return cfg().getInt("min-health", 1);
    }
    

    public String getMessage(String path, String... replacements) {
        String raw = cfg().getString(
                "messages." + path,
                "&cMessage missing: " + path
        );

        if (replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                raw = raw.replace(replacements[i], replacements[i + 1]);
            }
        }

        return formatMessage(raw);
    }
    

    public String formatMessage(String input) {
        if (input == null || input.isEmpty()) return "";

        if (input.indexOf('<') != -1 && input.indexOf('>') != -1) {
            try {
                Component component = mini.deserialize(input);
                return legacy.serialize(component);
            } catch (Exception ignored) {
            }
        }

        // Legacy fallback since i like them
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
