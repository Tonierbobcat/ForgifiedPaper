package com.loficostudios.forgified.paper.nametags;

import com.loficostudios.forgified.paper.ForgifiedPaper;
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
    private final ForgifiedPaperPlugin plugin;

    private final NameTagNMS nms;

    public NametagManager(ForgifiedPaperPlugin plugin) {
        this.nms = ForgifiedPaper.NAMETAG.create();

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

                nms.update(entity);
            }
        }, 0,1);
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;
        nms.remove(tag);

        // re-create
        plugin.runTaskLater(() -> {
            if (player.isOnline())
                nms.create(tag);
        }, 1L);
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;
        nms.remove(tag);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        var player = e.getPlayer();
        var tag = entities.get(player.getUniqueId());
        if (tag == null)
            return;

        // re-create on spawn
        plugin.runTaskLater(() -> {
            if (player.isOnline())
                nms.create(tag);
        }, 1L);
    }

    private void remove(NametagEntity entity) {
        nms.remove(entity);
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
        nms.create(tagEntity);
        entities.put(target.getUID(), tagEntity);
        return tag;
    }
}
