package org.fliff.lifeSteal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.fliff.lifeSteal.commands.ReloadCommand;
import org.fliff.lifeSteal.commands.ResetHeartsCommand;
import org.fliff.lifeSteal.commands.WithdrawHeartCommand;
import org.fliff.lifeSteal.listeners.PlayerDeathListener;
import org.fliff.lifeSteal.listeners.ReviveBeaconListener;
import org.fliff.lifeSteal.listeners.RightClickListener;
import org.fliff.lifeSteal.utils.RecipeManager;
import org.fliff.lifeSteal.utils.SlotRecipeManager;

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
        getCommand("withdrawheart").setExecutor(new WithdrawHeartCommand());
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
