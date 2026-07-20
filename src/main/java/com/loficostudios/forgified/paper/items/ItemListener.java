package com.loficostudios.forgified.paper.items;

import com.loficostudios.forgified.paper.ForgifiedPaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ItemListener implements Listener {
    private HashMap<UUID, Object[]> projectiles = new HashMap<>();

    @EventHandler
    private void onShoot(EntityShootBowEvent e) {
        if (e.isCancelled())
            return;
        var item = e.getBow();
        if (item == null || !item.hasItemMeta())
            return;
        var meta = Objects.requireNonNull(item.getItemMeta());
        var pdc = meta.getPersistentDataContainer();


        Object[] bow = null;
        for (ItemRegistry registry : ForgifiedPaperPlugin.registries) {
            var id = ItemRegistry.getItemID(item);
            if (id == null)
                continue;
            if (registry.getById(id) instanceof BowItem bowItem) {
                bow = new Object[] {item, bowItem };
                break;
            }
        }

        boolean isCustom = bow != null;
        if (!isCustom)
            return;

        ((BowItem) bow[1]).onShoot(e);
        projectiles.put(e.getProjectile().getUniqueId(), bow);
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent e) {
        var bow = projectiles.remove(e.getEntity().getUniqueId());
        if (bow == null)
            return;
        ((BowItem) bow[1]).onHit(e);
    }
}
