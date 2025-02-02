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
            double killerMaxHealth = killer.getMaxHealth();
            double killerCurrentHealth = killer.getHealth();
            double victimMaxHealth = victim.getMaxHealth();
            double minHealth = configManager.getMinHealth() * 2;
            double maxHearts = configManager.getMaxHealth() * 2;
            boolean noHpLossOnFullHealth = configManager.isNoHpLossOnFullHealth();

            // Check if killer has full HP and config prevents HP loss
            if (noHpLossOnFullHealth && killerCurrentHealth >= killerMaxHealth) return;

            // Prevent health dupe: If victim is at minimum health, killer doesn't gain HP
            if (victimMaxHealth <= minHealth) return;

            // Killer gains health (if not at max health)
            if (killerMaxHealth < maxHearts) killer.setMaxHealth(killerMaxHealth + 2);

            // Victim loses health (if above minimum health)
            victim.setMaxHealth(victimMaxHealth - 2);
        }
    }
}
