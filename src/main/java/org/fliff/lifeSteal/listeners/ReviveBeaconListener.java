package org.fliff.lifeSteal.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.fliff.lifeSteal.LifeSteal;
import org.fliff.lifeSteal.utils.ConfigManager;
import org.fliff.lifeSteal.utils.NBTUtils;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class ReviveBeaconListener implements Listener {

    private final Map<UUID, BlockDisplay> activeDisplays = new HashMap<>();
    private final Map<UUID, TextDisplay> activeText = new HashMap<>();
    private final Map<UUID, Location> ritualLocations = new HashMap<>();
    private final List<Display> allDisplays = new ArrayList<>();
    private final ConfigManager config = new ConfigManager();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!NBTUtils.hasNBTTag(meta, "ReviveBeacon")) return;

        event.setCancelled(true);

        if (activeDisplays.containsKey(player.getUniqueId())) {
            player.sendMessage(config.getMessage("already-active"));
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        Location loc = clicked.getLocation().clone().add(0.5, 2.5, 0.5); // ⬆️ 1 Block höher
        ritualLocations.put(player.getUniqueId(), loc);
        item.setAmount(item.getAmount() - 1);

        // Spawn Beacon BlockDisplay (hover only)
        BlockDisplay beacon = loc.getWorld().spawn(loc, BlockDisplay.class);
        beacon.setBlock(Material.BEACON.createBlockData());
        beacon.setTransformation(new Transformation(
                new Vector3f(-0.5f, -0.5f, -0.5f),
                new AxisAngle4f(),
                new Vector3f(1, 1, 1),
                new AxisAngle4f()
        ));
        beacon.setPersistent(false);
        activeDisplays.put(player.getUniqueId(), beacon);
        allDisplays.add(beacon);

        // Spawn floating text above beacon
        TextDisplay text = loc.getWorld().spawn(loc.clone().add(0, 1.2, 0), TextDisplay.class);
        text.setText("§bType banned player name...");
        text.setBillboard(Display.Billboard.CENTER);
        text.setSeeThrough(true);
        text.setShadowed(true);
        text.setBackgroundColor(Color.fromARGB(100, 0, 0, 0));
        text.setPersistent(false);
        activeText.put(player.getUniqueId(), text);
        allDisplays.add(text);

        player.sendMessage(config.getMessage("ritual-start"));
        loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1, 1.2f);

        startHover(beacon);
    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!activeDisplays.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        String input = event.getMessage();
        BlockDisplay beacon = activeDisplays.remove(player.getUniqueId());
        TextDisplay text = activeText.remove(player.getUniqueId());
        Location loc = ritualLocations.remove(player.getUniqueId());
        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        Bukkit.getScheduler().runTask(LifeSteal.getInstance(), () -> {
            if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(target.getName())) {
                player.sendMessage(config.getMessage("player-not-banned"));
                if (beacon.isValid()) beacon.remove();
                if (text.isValid()) text.remove();
                return;
            }

            text.setText("§eRitual started...");
            runReviveSequence(player, target, beacon, text, loc);
        });
    }

    private void startHover(BlockDisplay display) {
        new BukkitRunnable() {
            float tick = 0;

            @Override
            public void run() {
                if (!display.isValid()) {
                    cancel();
                    return;
                }

                tick += 0.1;
                float y = (float) Math.sin(tick) * 0.06f;

                display.setTransformation(new Transformation(
                        new Vector3f(-0.5f, -0.5f + y, -0.5f),
                        new AxisAngle4f(),
                        new Vector3f(1, 1, 1),
                        new AxisAngle4f()
                ));
            }
        }.runTaskTimer(LifeSteal.getInstance(), 0L, 1L);
    }

    private void runReviveSequence(Player player, OfflinePlayer target, BlockDisplay beacon, TextDisplay text, Location loc) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                World world = loc.getWorld();
                tick++;

                if (tick == 20) {
                    text.setText("§bGathering energy...");
                    world.spawnParticle(Particle.ENCHANT, loc.clone().add(0, 0.8, 0), 25, 0.4, 0.3, 0.4);
                    world.playSound(loc, Sound.BLOCK_PORTAL_TRIGGER, 1, 1.2f);
                }

                if (tick == 40) {
                    text.setText("§bReviving §e" + target.getName() + "§b...");
                    world.spawnParticle(Particle.GLOW, loc.clone().add(0, 0.6, 0), 35, 0.2, 0.3, 0.2);
                    world.playSound(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 1.5f);
                }

                if (tick == 90) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                    text.setText("§a" + target.getName() + " revived!");
                    player.sendMessage(config.getMessage("revive-success", "{player}", target.getName()));

                    world.spawnParticle(Particle.TOTEM_OF_UNDYING, loc.clone().add(0, 1.0, 0), 50, 0.3, 0.4, 0.3);
                    world.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f, 1.4f);
                    if (beacon.isValid()) beacon.remove();
                }

                if (tick == 110) {
                    if (beacon.isValid()) beacon.remove(); //Douple Check just so nothing happns ^^
                    if (text.isValid()) text.remove();
                    cancel();
                }
            }
        }.runTaskTimer(LifeSteal.getInstance(), 0L, 1L);
    }

    public void cleanupDisplays() {
        for (Display display : allDisplays) {
            if (display != null && display.isValid()) display.remove();
        }
        allDisplays.clear();
    }
}
