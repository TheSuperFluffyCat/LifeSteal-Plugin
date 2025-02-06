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
        if (killer == null) return;

        // Min / Max Health Limits
        double minHealth = configManager.getMinHealth() * 2;      // e.g., 1 heart (2 HP)
        double maxHealthLimit = configManager.getMaxHealth() * 2; // Max allowed health in HP
        boolean noHpLossOnFullHealth = configManager.isNoHpLossOnFullHealth();

        double killerCurrentHealth = killer.getHealth();
        double killerMaxHealth = killer.getMaxHealth();
        double victimMaxHealth = victim.getMaxHealth();

        // I hope i fixed the bugs now
        if (noHpLossOnFullHealth && killerCurrentHealth >= killerMaxHealth) return;

        if (victimMaxHealth <= minHealth) return;

        victim.setMaxHealth(Math.max(victimMaxHealth - 2, minHealth));

        if (killerMaxHealth < maxHealthLimit) {
            killer.setMaxHealth(killerMaxHealth + 2);
        }
    }
}
