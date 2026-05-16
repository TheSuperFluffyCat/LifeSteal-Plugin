package de.survivalnight.luna.lifeSteal.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.survivalnight.luna.lifeSteal.LifeSteal;
import de.survivalnight.luna.lifeSteal.utils.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResetAllHeartsCommand implements CommandExecutor {

    private final ConfigManager configManager = new ConfigManager();
    private final LifeSteal plugin;

    public ResetAllHeartsCommand(LifeSteal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lifesteal.resetall")) {
            sender.sendMessage(configManager.formatMessage("&cYou don't have permission to use this command!"));
            return true;
        }

        sender.sendMessage(configManager.formatMessage("&eResetting all players' hearts..."));

        int onlineCount = 0;
        int offlineCount = 0;
        Set<UUID> offlinePlayerUUIDs = new HashSet<>();

        // Reset all online players immediately
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setMaxHealth(20);
            player.setHealth(20);
            onlineCount++;
        }

        // Collect all offline players
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            // Only process players who have played before and are not currently online
            if (offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                offlinePlayerUUIDs.add(offlinePlayer.getUniqueId());
                offlineCount++;
            }
        }

        // Save offline player UUIDs to a file for processing on join
        savePendingResets(offlinePlayerUUIDs);

        sender.sendMessage(configManager.formatMessage("&aReset complete!"));
        sender.sendMessage(configManager.formatMessage("&7Online players reset: &a" + onlineCount));
        sender.sendMessage(configManager.formatMessage("&7Offline players queued: &a" + offlineCount));
        sender.sendMessage(configManager.formatMessage("&7Offline players will be reset when they next join."));

        return true;
    }

    private void savePendingResets(Set<UUID> uuids) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File pendingFile = new File(dataFolder, "pending_resets.txt");
        try (FileWriter writer = new FileWriter(pendingFile, false)) {
            for (UUID uuid : uuids) {
                writer.write(uuid.toString() + "\n");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save pending resets: " + e.getMessage());
        }
    }
}
