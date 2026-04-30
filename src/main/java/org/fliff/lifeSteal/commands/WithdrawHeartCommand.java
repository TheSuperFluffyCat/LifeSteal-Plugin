package de.survivalnight.luna.lifeSteal.commands;

import de.survivalnight.luna.lifeSteal.utils.ConfigManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class WithdrawHeartCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final NamespacedKey heartKey;

    public WithdrawHeartCommand(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.heartKey = new NamespacedKey(plugin, "lifesteal_heart_item"); // eindeutig
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(configManager.formatMessage("&cOnly players can use this command!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(configManager.formatMessage("&cUsage: /withdrawheart <amount>"));
            return true;
        }

        final long amountLong;
        try {
            amountLong = Long.parseLong(args[0].trim());
        } catch (NumberFormatException e) {
            player.sendMessage(configManager.formatMessage("&cInvalid number!"));
            return true;
        }

        if (amountLong <= 0) {
            player.sendMessage(configManager.formatMessage("&cInvalid number!"));
            return true;
        }

        // Hard-cap since of a bit error
        if (amountLong > 64L) {
            player.sendMessage(configManager.formatMessage("&cYou can only withdraw up to 64 hearts at once!"));
            return true;
        }

        final int amount = (int) amountLong;

        // Health / min-health safety
        final double minHeartsCfg = configManager.getMinHealth();
        if (minHeartsCfg < 1) {
            player.sendMessage(configManager.formatMessage("&cServer misconfigured (min health). Please contact staff."));
            return true;
        }

        final double minHealth = minHeartsCfg * 2.0; // hearts -> health points
        final double playerMaxHealth = getMaxHealth(player);

        // Wenn irgendwas weird ist
        if (!(playerMaxHealth > 0) || Double.isNaN(playerMaxHealth) || Double.isInfinite(playerMaxHealth)) {
            player.sendMessage(configManager.formatMessage("&cYour health attributes look invalid right now."));
            return true;
        }

        final double withdrawHealthPoints = amount * 2.0;

        final double newMaxHealth = playerMaxHealth - withdrawHealthPoints;

        if (newMaxHealth < minHealth - 1e-9) {
            player.sendMessage(configManager.formatMessage("&cYou don't have enough hearts to withdraw!"));
            return true;
        }

        final ItemStack heartItem = createHeartItem(amount);

        // check inventory
        if (!canFullyFit(player.getInventory(), heartItem)) {
            player.sendMessage(configManager.formatMessage("&cYou don't have enough space in your inventory!"));
            return true;
        }

        final double oldMaxHealth = playerMaxHealth;
        final double oldHealth = player.getHealth();

        try {
            // Apply new max
            setMaxHealth(player, newMaxHealth);

            // Clamp current health if above new max (prevents weirdness)
            if (player.getHealth() > newMaxHealth) {
                player.setHealth(newMaxHealth);
            }

            // Give item (should fit, but still do safe add + rollback)
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(heartItem);
            if (leftovers != null && !leftovers.isEmpty()) {
                // Something interfered between check and add (rare, but possible! T~T)
                // Remove any inserted part and rollback health
                int leftoverAmount = leftovers.values().stream().mapToInt(ItemStack::getAmount).sum();
                int inserted = amount - leftoverAmount;

                if (inserted > 0) {
                    removeMatchingHearts(player.getInventory(), inserted);
                }

                // rollback health
                setMaxHealth(player, oldMaxHealth);
                player.setHealth(Math.min(oldHealth, oldMaxHealth));

                player.sendMessage(configManager.formatMessage("&cYou don't have enough space in your inventory!"));
                return true;
            }

            player.sendMessage(
                    configManager.getMessage(
                            "success_withdraw",
                            "%amount%", String.valueOf(amount)
                    )
            );
            return true;

        } catch (Throwable t) {
            // rollback on ANY unexpected error (for more protection)
            try {
                setMaxHealth(player, oldMaxHealth);
                player.setHealth(Math.min(oldHealth, oldMaxHealth));
            } catch (Throwable ignored) {}

            player.sendMessage(configManager.formatMessage("&cSomething went wrong. Nothing was withdrawn."));
            return true;
        }
    }

    private ItemStack createHeartItem(int amount) {
        ItemStack item = new ItemStack(getConfiguredHeartMaterial(), amount);
        ItemMeta meta = item.getItemMeta();

        String name = configManager.getHeartItemName();
        if (name != null && !name.isEmpty()) {
            meta.setDisplayName(name);
        }


        meta.getPersistentDataContainer().set(heartKey, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }



    private Material getConfiguredHeartMaterial() {
        File recipeFile = new File(plugin.getDataFolder(), "recipes.yml");
        if (!recipeFile.exists()) return Material.NETHER_STAR;

        String matName = YamlConfiguration.loadConfiguration(recipeFile)
                .getString("heart-item.result.type", "NETHER_STAR");
        Material material = Material.matchMaterial(matName);
        return material != null ? material : Material.NETHER_STAR;
    }
    private boolean canFullyFit(Inventory inv, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return true;

        int remaining = stack.getAmount();
        int maxStack = Math.min(stack.getMaxStackSize(), 64);

        for (ItemStack existing : inv.getContents()) {
            if (existing == null || existing.getType() == Material.AIR) continue;
            if (!existing.isSimilar(stack)) continue;

            int space = maxStack - existing.getAmount();
            if (space <= 0) continue;

            remaining -= Math.min(space, remaining);
            if (remaining <= 0) return true;
        }

        int emptySlots = 0;
        for (ItemStack existing : inv.getContents()) {
            if (existing == null || existing.getType() == Material.AIR) emptySlots++;
        }

        int slotsNeeded = (int) Math.ceil(remaining / (double) maxStack);
        return emptySlots >= slotsNeeded;
    }

    private void removeMatchingHearts(Inventory inv, int amount) {
        if (amount <= 0) return;

        int toRemove = amount;

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack it = inv.getItem(i);
            if (it == null || it.getType() == Material.AIR) continue;

            ItemMeta meta = it.getItemMeta();
            if (meta == null) continue;

            Byte marker = meta.getPersistentDataContainer().get(heartKey, PersistentDataType.BYTE);
            if (marker == null || marker != (byte) 1) continue;

            int take = Math.min(toRemove, it.getAmount());
            it.setAmount(it.getAmount() - take);
            if (it.getAmount() <= 0) inv.setItem(i, null);

            toRemove -= take;
            if (toRemove <= 0) break;
        }
    }

    private double getMaxHealth(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) return attr.getBaseValue();
        return player.getMaxHealth();
    }

    private void setMaxHealth(Player player, double value) {
        if (value < 1.0) value = 1.0;

        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(value);
        } else {
            player.setMaxHealth(value);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return null;

        if (args.length == 1) {
            double minHealth = Math.max(1, configManager.getMinHealth()) * 2.0;
            double maxHealth = getMaxHealth(player);

            double maxWithdrawHeartsExact = (maxHealth - minHealth) / 2.0;
            long maxHearts = (long) Math.floor(maxWithdrawHeartsExact + 1e-9);

            maxHearts = Math.min(maxHearts, 64L);
            if (maxHearts < 1) return Collections.emptyList();

            List<String> suggestions = new ArrayList<>();
            for (long i = 1; i <= maxHearts; i++) suggestions.add(String.valueOf(i));
            return suggestions;
        }

        return null;
    }
}




