package org.fliff.lifeSteal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.fliff.lifeSteal.commands.ResetHeartsCommand;
import org.fliff.lifeSteal.commands.WithdrawHeartCommand;
import org.fliff.lifeSteal.listeners.PlayerDeathListener;
import org.fliff.lifeSteal.listeners.RightClickListener;

public final class LifeSteal extends JavaPlugin {

    private static LifeSteal instance;

    public static LifeSteal getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Load Config
        saveDefaultConfig();

        // Register Commands
        getCommand("resethearts").setExecutor(new ResetHeartsCommand());
        getCommand("withdrawheart").setExecutor(new WithdrawHeartCommand());

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);

        getLogger().info("LifeSteal Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LifeSteal Plugin has been disabled!");
    }
}
