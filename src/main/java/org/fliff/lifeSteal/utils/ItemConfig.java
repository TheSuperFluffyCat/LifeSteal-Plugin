package org.fliff.lifeSteal.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class handles the creation of all special plugin items like Heart Items or Revive Beacons.
 * Item data (e.g. name) is partly configurable via config.yml.
 */
public class ItemConfig {

    private static final ConfigManager configManager = new ConfigManager();

    // 1️⃣ Create a Heart Item with the configured display name and NBT
    public static ItemStack getHeartItem(int amount) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(configManager.getHeartItemName());
        NBTUtils.addNBTTag(meta, "HeartItem", "1");
        item.setItemMeta(meta);
        return item;
    }

    // 2️⃣ Create a Revive Beacon with custom display name and NBT tag
    public static ItemStack getReviveBeacon() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bRevive Beacon");
        NBTUtils.addNBTTag(meta, "ReviveBeacon", "1");
        item.setItemMeta(meta);
        return item;
    }
}
