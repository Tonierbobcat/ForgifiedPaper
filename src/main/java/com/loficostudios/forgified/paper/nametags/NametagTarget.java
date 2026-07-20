package com.loficostudios.forgified.paper.nametags;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface NametagTarget {
    UUID getUID();
    Location getLocation();
    boolean isValid();

    static NametagTarget livingEntity(LivingEntity entity) {
        if (entity instanceof Player player)
            return new PlayerNametagTarget(player);
        return new BukkitNametagTarget(entity);
    }

    static NametagTarget player(Player player) {
        return new PlayerNametagTarget(player);
    }
}
