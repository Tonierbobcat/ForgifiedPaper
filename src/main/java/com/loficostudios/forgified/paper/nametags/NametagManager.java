package com.loficostudios.forgified.paper.nametags;

import com.loficostudios.forgified.paper.ForgifiedPaperPlugin;
import com.loficostudios.forgified.paper.utils.VersionHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NametagManager implements Listener {
    private static final String minVersion = "com.loficostudios.forgified.paper.nametags.NameTagNMS_1_19_4";

    private final VersionHandler<NameTagNMS> nms = new VersionHandler<>(minVersion);
    private final JavaPlugin plugin;

    public NametagManager(ForgifiedPaperPlugin plugin) {
        nms.init();

        this.plugin = plugin;
        plugin.runTaskTimer(() -> {
            var entities = NametagManager.this.entities.values().toArray(NametagEntity[]::new);
            for (NametagEntity entity : entities) {
                var target = entity.getTarget();
                if (target instanceof PlayerTarget player) {
                    if (!player.isOnline()) {
                        remove(entity);
                        return;
                    }
                } else {
                    if (!target.isValid()) {
                        remove(entity);
                        return;
                    }
                }

                nms.get().update(entity);
            }
        }, 0,1);
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;
        nms.get().remove(tag);

        // re-create
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                nms.get().create(tag);
            }
        }, 1L);
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;
        nms.get().remove(tag);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;
        // re-create on spawn
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                nms.get().create(tag);
            }
        }, 1L);
    }

    private void remove(NametagEntity entity) {
        nms.get().remove(entity);
        entities.remove(entity.getUID());
    }

    private final Map<UUID, NametagEntity> entities = new HashMap<>();

    public void remove(NametagTarget target) {
        var entity = entities.get(target.getUID());
        if (entity != null) {
            remove(entity);
        }
    }

    public NameTag create(NametagTarget target, List<Component> lines) {
        var tag = new NameTag(lines);
        var tagEntity = new NametagEntityImpl(target, tag);
        nms.get().create(tagEntity);
        entities.put(target.getUID(), tagEntity);
        return tag;
    }
}
