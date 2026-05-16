package de.survivalnight.luna.lifeSteal.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import de.survivalnight.luna.lifeSteal.LifeSteal;
import de.survivalnight.luna.lifeSteal.utils.ConfigManager;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final LifeSteal plugin;
    private final ConfigManager configManager = new ConfigManager();

    public PlayerJoinListener(LifeSteal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Set<UUID> pendingResets = loadPendingResets();
        
        if (pendingResets.contains(playerUUID)) {
            // Reset the player's hearts
            player.setMaxHealth(20);
            player.setHealth(20);
            
            // Remove this player from pending resets
            pendingResets.remove(playerUUID);
            savePendingResets(pendingResets);
            
            player.sendMessage(configManager.formatMessage("&aYour hearts have been reset!"));
            plugin.getLogger().info("Reset hearts for player " + player.getName() + " (offline reset applied)");
        }
    }

    private Set<UUID> loadPendingResets() {
        Set<UUID> uuids = new HashSet<>();
        File pendingFile = new File(plugin.getDataFolder(), "pending_resets.txt");
        
        if (!pendingFile.exists()) {
            return uuids;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pendingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    uuids.add(UUID.fromString(line.trim()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in pending resets: " + line);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load pending resets: " + e.getMessage());
        }

        return uuids;
    }

    private void savePendingResets(Set<UUID> uuids) {
        File pendingFile = new File(plugin.getDataFolder(), "pending_resets.txt");
        
        try (FileWriter writer = new FileWriter(pendingFile, false)) {
            for (UUID uuid : uuids) {
                writer.write(uuid.toString() + "\n");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save pending resets: " + e.getMessage());
        }
    }
}
