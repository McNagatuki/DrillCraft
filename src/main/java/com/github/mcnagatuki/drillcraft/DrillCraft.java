package com.github.mcnagatuki.drillcraft;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class DrillCraft extends JavaPlugin implements Listener {
    public static DrillCraft plugin;
    public Config config;
    private Map<Player, Float> sumTheta;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        config = new Config();
        sumTheta = new HashMap<>();

        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("drill").setExecutor(new CommandManager());
    }

    // ゲーム開始！
    public void start() {
        if (config.running) return;
        config.running = true;
    }

    // ゲーム終了！
    public void stop() {
        config.running = false;
    }

    // 回転角の導出と掘削
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!config.running) return;

        Player player = event.getPlayer();

        // game mode validation
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        Location prev = event.getFrom();
        Location next = event.getTo();

        float prevYaw = Location.normalizeYaw(prev.getYaw());
        float nextYaw = Location.normalizeYaw(next.getYaw());

        // 大雑把
        float diff = nextYaw - prevYaw;
        if (diff > 0) {
            if (diff > 360 - diff) {
                diff = diff - 360;
            }
        } else {
            if (-diff > diff + 360) {
                diff = diff + 360;
            }
        }

        // direction check
        if (config.directed && diff < 0) return;

        diff = Math.abs(diff);

        float theta = sumTheta.containsKey(player) ? sumTheta.get(player) + diff : diff;

        // 掘削
        while (theta >= config.theta) {
            theta -= config.theta;

            Location loc = player.getLocation();
            loc.setY(loc.getY() - 1);

            Block block = loc.getBlock();

            // air is ignored
            if (block.getType() == Material.AIR) continue;

            Sound sound = block.getSoundGroup().getBreakSound();
            loc.getWorld().playSound(loc, sound, 1, 1);

            // アイテム化する場合としない場合
            if (config.droppable) {
                block.breakNaturally();
            } else {
                block.setType(Material.AIR);
            }
        }

        sumTheta.put(player, theta);
    }

    // changeable params
    public class Config {
        double theta = 30;
        boolean directed = false;
        boolean running = false;
        boolean droppable = false;
    }
}
