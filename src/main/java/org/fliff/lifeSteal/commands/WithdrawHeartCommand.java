package de.survivalnight.luna.lifeSteal.commands;

import de.survivalnight.luna.lifeSteal.utils.ConfigManager;
import de.survivalnight.luna.lifeSteal.utils.NBTUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WithdrawHeartCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager = new ConfigManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.formatMessage("&cOnly players can use this command!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(configManager.formatMessage("&cUsage: /withdrawheart <amount>"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(configManager.formatMessage("&cInvalid number!"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(configManager.formatMessage("&cInvalid number!"));
            return true;
        }

        double playerMaxHealth = player.getMaxHealth();
        double minHealth = configManager.getMinHealth() * 2;

        long maxWithdrawableHearts = (long) ((playerMaxHealth - minHealth) / 2);

        if (amount > maxWithdrawableHearts) {
            player.sendMessage(configManager.formatMessage("&cYou don't have enough hearts to withdraw!"));
            return true;
        }

        if (amount > 64) {
            player.sendMessage(configManager.formatMessage("&cYou can only withdraw up to 64 hearts at once!"));
            return true;
        }

        ItemStack heartItem = createHeartItem((int) amount);
        if (!player.getInventory().addItem(heartItem).isEmpty()) {
            player.sendMessage(configManager.formatMessage("&cYou don't have enough space in your inventory!"));
            return true;
        }

        double newMaxHealth = playerMaxHealth - (amount * 2);
        if (newMaxHealth < minHealth) {
            player.sendMessage(configManager.formatMessage("&cYou don't have enough hearts to withdraw!"));
            return true;
        }

        player.setMaxHealth(newMaxHealth);
        player.sendMessage(configManager.formatMessage("&aSuccessfully withdrew " + amount + " heart(s)."));
        return true;
    }

    private ItemStack createHeartItem(int amount) {
        ItemStack heartItem = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = heartItem.getItemMeta();
        meta.setDisplayName(configManager.getHeartItemName());
        NBTUtils.addNBTTag(meta, "HeartItem", "1");
        heartItem.setItemMeta(meta);
        return heartItem;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            double playerMaxHealth = player.getMaxHealth();
            double minHealth = configManager.getMinHealth() * 2;

            long maxWithdrawableHearts = (long) ((playerMaxHealth - minHealth) / 2);

            for (long i = 1; i <= maxWithdrawableHearts && i <= 64; i++) {
                suggestions.add(String.valueOf(i));
            }

            return suggestions;
        }

        return null;
    }
}
