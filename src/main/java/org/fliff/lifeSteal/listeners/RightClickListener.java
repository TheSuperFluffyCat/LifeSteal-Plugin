package de.survivalnight.luna.lifeSteal.listeners;

import de.survivalnight.luna.lifeSteal.LifeSteal;
import de.survivalnight.luna.lifeSteal.utils.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RightClickListener implements Listener {

    private final ConfigManager configManager = new ConfigManager();
    private final NamespacedKey heartKey =
            new NamespacedKey(LifeSteal.getInstance(), "lifesteal_heart_item");
    private final NamespacedKey legacyHeartKey =
            new NamespacedKey(LifeSteal.getInstance(), "HeartItem");

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        boolean isHeartItem = meta.getPersistentDataContainer().has(heartKey, PersistentDataType.BYTE)
                || meta.getPersistentDataContainer().has(legacyHeartKey, PersistentDataType.STRING);
        if (!isHeartItem) return;

        double maxHealth = player.getMaxHealth();
        double maxAllowedHealth = configManager.getMaxHealth() * 2;

        if (maxHealth >= maxAllowedHealth) {
            player.sendActionBar(
                    configManager.getMessage("no_hearts")
            );
            return;
        }

        int amount = player.isSneaking() ? item.getAmount() : 1;
        int redeemableAmount = Math.min(
                amount,
                (int) ((maxAllowedHealth - maxHealth) / 2)
        );

        item.setAmount(item.getAmount() - redeemableAmount);
        player.setMaxHealth(maxHealth + redeemableAmount * 2);

        player.sendActionBar(
                configManager.getMessage(
                        "success_redeem",
                        "%amount%", String.valueOf(redeemableAmount)
                )
        );
    }
}
