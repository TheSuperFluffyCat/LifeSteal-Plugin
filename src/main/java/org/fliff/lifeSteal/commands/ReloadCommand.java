package org.fliff.lifeSteal.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.fliff.lifeSteal.LifeSteal;
import org.fliff.lifeSteal.utils.ConfigManager;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager = new ConfigManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || sender.hasPermission("lifesteal.reload")) {
            LifeSteal.getInstance().reloadConfig();
            configManager.reloadConfig(); // Konfiguration neu laden!
            sender.sendMessage("§x§4§E§F§B§0§7✅ §x§5§D§E§6§0§8L§x§6§D§D§2§0§8i§x§7§C§B§D§0§9f§x§8§C§A§9§0§Ae§x§9§B§9§4§0§AS§x§A§A§8§0§0§Bt§x§B§A§6§B§0§Ce§x§C§9§5§7§0§Da§x§D§9§4§2§0§Dl §x§E§8§2§E§0§EC§x§F§7§1§9§0§Fo§x§F§F§1§7§1§7n§x§F§F§2§6§2§6f§x§F§F§3§6§3§6i§x§F§F§4§5§4§5g §x§F§F§5§5§5§5R§x§F§F§6§5§6§5e§x§F§F§7§4§7§4l§x§F§F§8§4§8§4o§x§F§F§9§3§9§3a§x§F§F§A§3§A§3d§x§F§F§B§2§B§2e§x§F§F§C§2§C§2d");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? Collections.singletonList("reload") : Collections.emptyList();
    }
}
