package org.fliff.lifeSteal.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fliff.lifeSteal.utils.ConfigManager;
import org.fliff.lifeSteal.utils.NBTUtils;

public class RightClickListener implements Listener {

    private final ConfigManager configManager = new ConfigManager();

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the item is a Heart Item
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!NBTUtils.hasNBTTag(meta, "HeartItem")) return;

        double maxHealth = player.getMaxHealth(); // Bukkit uses 20 for 10 hearts
        double maxAllowedHealth = configManager.getMaxHealth() * 2; // Convert config value to half-hearts

        // Prevent exceeding max health
        if (maxHealth >= maxAllowedHealth) {
            player.sendActionBar(configManager.formatMessage("&cYou can't redeem more hearts!"));
            return;
        }

        // Handle sneaking for redeeming multiple items
        int amount = player.isSneaking() ? item.getAmount() : 1;
        int redeemableAmount = Math.min(amount, (int) ((maxAllowedHealth - maxHealth) / 2)); // Adjust for half-hearts

        // Redeem the hearts
        item.setAmount(item.getAmount() - redeemableAmount);
        player.setMaxHealth(maxHealth + redeemableAmount * 2); // Add health in half-hearts

        player.sendActionBar(configManager.formatMessage(
                "&aYou successfully redeemed " + redeemableAmount + " heart(s)!"
        ));
    }
}
