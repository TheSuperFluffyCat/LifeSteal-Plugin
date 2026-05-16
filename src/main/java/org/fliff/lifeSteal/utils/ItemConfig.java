package de.survivalnight.luna.lifeSteal.utils;

import de.survivalnight.luna.lifeSteal.LifeSteal;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;

/**
 * This class handles the creation of all special plugin items like Heart Items or Revive Beacons.
 * 
 * Item data (e.g. name) is partly configurable via config.yml.
 * ToDo: Better Config overlay for that
 */
public class ItemConfig {

    private static final ConfigManager configManager = new ConfigManager();

    //  Create a Heart Item with the configured display name and NBT
    public static ItemStack getHeartItem(int amount) {
        ItemStack item = new ItemStack(getConfiguredHeartMaterial(), amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(configManager.getHeartItemName());
        meta.getPersistentDataContainer().set(
                new NamespacedKey(LifeSteal.getInstance(), "lifesteal_heart_item"),
                PersistentDataType.BYTE,
                (byte) 1
        );
        item.setItemMeta(meta);
        return item;
    }

    private static Material getConfiguredHeartMaterial() {
        File recipeFile = new File(LifeSteal.getInstance().getDataFolder(), "recipes.yml");
        if (!recipeFile.exists()) return Material.NETHER_STAR;

        String materialName = YamlConfiguration.loadConfiguration(recipeFile)
                .getString("heart-item.result.type", "NETHER_STAR");
        Material material = Material.matchMaterial(materialName);
        return material != null ? material : Material.NETHER_STAR;
    }

    // Create a Revive Beacon with custom display name and NBT tag
    public static ItemStack getReviveBeacon() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bRevive Beacon");
        NBTUtils.addNBTTag(meta, "ReviveBeacon", "1");
        item.setItemMeta(meta);
        return item;
    }
}
