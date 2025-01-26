package org.fliff.lifeSteal.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.fliff.lifeSteal.LifeSteal;

import java.util.HashMap;
import java.util.Map;

public class NBTUtils {

    // Simulates adding an NBT tag
    public static void addNBTTag(ItemMeta meta, String key, String value) {
        meta.getPersistentDataContainer().set(new NamespacedKey(LifeSteal.getInstance(), key), PersistentDataType.STRING, value);
    }

    // Simulates checking an NBT tag
    public static boolean hasNBTTag(ItemMeta meta, String key) {
        return meta.getPersistentDataContainer().has(new NamespacedKey(LifeSteal.getInstance(), key), PersistentDataType.STRING);
    }
}
