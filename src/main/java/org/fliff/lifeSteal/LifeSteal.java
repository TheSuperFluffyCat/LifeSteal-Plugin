package de.survivalnight.luna.lifeSteal;

import de.survivalnight.luna.lifeSteal.commands.ReloadCommand;
import de.survivalnight.luna.lifeSteal.commands.ResetHeartsCommand;
import de.survivalnight.luna.lifeSteal.commands.WithdrawHeartCommand;
import de.survivalnight.luna.lifeSteal.listeners.PlayerDeathListener;
import de.survivalnight.luna.lifeSteal.listeners.ReviveBeaconListener;
import de.survivalnight.luna.lifeSteal.listeners.RightClickListener;
import de.survivalnight.luna.lifeSteal.utils.ConfigManager;
import de.survivalnight.luna.lifeSteal.utils.SlotRecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeSteal extends JavaPlugin {

    private static LifeSteal instance;

    public static LifeSteal getInstance() {
        return instance;
    }

    private ReviveBeaconListener beaconListener;

    @Override
    public void onEnable() {
        instance = this;
        beaconListener = new ReviveBeaconListener();

        // Load Config
        saveDefaultConfig();
        new SlotRecipeManager(this).registerAll();

        // Register Commands
        getCommand("resethearts").setExecutor(new ResetHeartsCommand());
        getCommand("withdrawheart").setExecutor(new WithdrawHeartCommand(this, new ConfigManager()));

        getCommand("lifestealplugin").setExecutor(new ReloadCommand());

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new ReviveBeaconListener(), this);

        getLogger().info("LifeSteal Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (beaconListener != null) {
            beaconListener.cleanupDisplays(); // entfernt alle Block/TextDisplays
        }
        getLogger().info("LifeSteal Plugin has been disabled!");
    }
}
