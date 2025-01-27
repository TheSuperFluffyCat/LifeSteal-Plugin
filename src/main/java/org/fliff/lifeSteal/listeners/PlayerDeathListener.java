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

            // Check if the killer is at full health and the config allows no HP loss
            if (noHpLossOnFullHealth && killerCurrentHealth >= killerMaxHealth) {
                return; // Skip health deduction for the victim
            }

            // Killer gains health (if not at max health)
            if (killerMaxHealth < maxHearts) {
                killer.setMaxHealth(killerMaxHealth + 2); // Gain 1 heart (2 health)
            }

            // Victim loses health (if above minimum health)
            if (victimMaxHealth > minHealth) {
                victim.setMaxHealth(victimMaxHealth - 2); // Lose 1 heart (2 health)
            }
        }
    }
}
