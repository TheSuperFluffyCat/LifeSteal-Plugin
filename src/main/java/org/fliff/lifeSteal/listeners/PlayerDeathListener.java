package org.fliff.lifeSteal.listeners;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.fliff.lifeSteal.LifeSteal;
import org.fliff.lifeSteal.utils.ConfigManager;
import org.fliff.lifeSteal.utils.ItemConfig;

import java.util.Date;

/**
 * This listener handles the core life steal logic when a player is killed.
 * The victim loses a heart, and the killer gains one (or receives a heart item).
 * If enabled, players are banned when reaching 0 hearts.
 */
public class PlayerDeathListener implements Listener {

    private final ConfigManager configManager = new ConfigManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        double minHealth = configManager.getMinHealth() * 2;
        double maxHealth = configManager.getMaxHealth() * 2;
        boolean banOnZero = LifeSteal.getInstance().getConfig().getBoolean("ban-on-zero-hearts", false);

        double victimHealth = victim.getMaxHealth();
        double killerHealth = killer.getMaxHealth();

        // 1️⃣ Check if victim is already at min health
        if (victimHealth <= minHealth) {
            if (banOnZero) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(victim.getName(), "You lost your last heart!", null, "LifeSteal");
                victim.kickPlayer("§cYou lost your last heart and were banned!");
            }
            return;
        }

        // 2️⃣ Reduce victim's max health by 1 heart (2 HP)
        victim.setMaxHealth(Math.max(victimHealth - 2, minHealth));

        // 3️⃣ Give killer a heart or item
        if (killerHealth >= maxHealth) {
            killer.getInventory().addItem(ItemConfig.getHeartItem(1));
        } else {
            killer.setMaxHealth(killerHealth + 2);
        }
    }
}
