package org.fliff.lifeSteal.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.fliff.lifeSteal.utils.ConfigManager;

public class PlayerDeathListener implements Listener {

    private final ConfigManager configManager = new ConfigManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && killer instanceof Player) {
            double maxHealth = killer.getMaxHealth();
            double minHealth = configManager.getMinHealth() * 2;
            double maxHearts = configManager.getMaxHealth() * 2;

            if (maxHealth < maxHearts) {
                killer.setMaxHealth(maxHealth + 2);
            }

            if (victim.getMaxHealth() > minHealth) {
                victim.setMaxHealth(victim.getMaxHealth() - 2);
            }
        }
    }
}
