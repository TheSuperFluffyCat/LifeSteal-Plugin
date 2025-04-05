package org.fliff.lifeSteal.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.fliff.lifeSteal.LifeSteal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and registers crafting recipes from recipes.yml using slot-based input (1–9).
 */
public class SlotRecipeManager {

    private final LifeSteal plugin;
    private FileConfiguration recipeConfig;

    public SlotRecipeManager(LifeSteal plugin) {
        this.plugin = plugin;
        loadRecipeFile();
    }

    public void registerAll() {
        if (recipeConfig == null) return;

        for (String key : recipeConfig.getKeys(false)) {
            ConfigurationSection recipe = recipeConfig.getConfigurationSection(key);
            if (recipe == null || !recipe.getBoolean("enabled", true)) continue;

            ConfigurationSection resultSection = recipe.getConfigurationSection("result");
            if (resultSection == null) continue;

            Material resultMat = Material.matchMaterial(resultSection.getString("type", "DIRT"));
            int resultAmount = resultSection.getInt("amount", 1);
            if (resultMat == null) continue;

            ItemStack result = new ItemStack(resultMat, resultAmount);
            ItemMeta meta = result.getItemMeta();

            // Auto-NBT & DisplayName
            if (key.equalsIgnoreCase("heart-item")) {
                meta.setDisplayName(new ConfigManager().getHeartItemName());
                NBTUtils.addNBTTag(meta, "HeartItem", "1");
            } else if (key.equalsIgnoreCase("revive-beacon")) {
                meta.setDisplayName("§bRevive Beacon");
                NBTUtils.addNBTTag(meta, "ReviveBeacon", "1");
            }

            result.setItemMeta(meta);

            ConfigurationSection slots = recipe.getConfigurationSection("slots");
            if (slots == null) continue;

            Map<Character, ItemStack> ingredientMap = new HashMap<>();
            String[] shape = new String[3];
            char currentChar = 'A';

            for (int row = 0; row < 3; row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 1; col <= 3; col++) {
                    int index = row * 3 + col;
                    ConfigurationSection slotSection = slots.getConfigurationSection(String.valueOf(index));
                    if (slotSection != null) {
                        Material mat = Material.matchMaterial(slotSection.getString("type", ""));
                        int amount = slotSection.getInt("amount", 1);
                        if (mat != null) {
                            char symbol = currentChar++;
                            line.append(symbol);

                            ItemStack ingredient = new ItemStack(mat, amount);
                            ingredientMap.put(symbol, ingredient);
                        } else {
                            line.append(" ");
                        }
                    } else {
                        line.append(" ");
                    }
                }
                shape[row] = line.toString();
            }

            ShapedRecipe shaped = new ShapedRecipe(new NamespacedKey(plugin, key), result);
            shaped.shape(shape[0], shape[1], shape[2]);

            for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
                shaped.setIngredient(entry.getKey(), entry.getValue().getType());
            }

            Bukkit.addRecipe(shaped);
        }
    }

    private void loadRecipeFile() {
        File file = new File(plugin.getDataFolder(), "recipes.yml");
        if (!file.exists()) {
            plugin.saveResource("recipes.yml", false); // optional: include default in jar
        }
        recipeConfig = YamlConfiguration.loadConfiguration(file);
    }
}
