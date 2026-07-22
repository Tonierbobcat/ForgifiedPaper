package com.loficostudios.forgified.paper.items;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class BowItem extends JItem {
    public BowItem(Properties properties) {
        /// Base Material MUST BE A BOW
        super(Material.BOW, properties);
    }

    public void onHit(ProjectileHitEvent e) {
    }

    public void onShoot(EntityShootBowEvent e) {
    }
}
