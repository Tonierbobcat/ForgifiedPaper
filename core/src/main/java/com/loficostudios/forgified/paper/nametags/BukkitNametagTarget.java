package com.loficostudios.forgified.paper.nametags;

import org.bukkit.entity.LivingEntity;

import java.util.function.Supplier;

public class BukkitNametagTarget extends AbstractNametagTarget implements BukkitTarget {

    private final Supplier<Integer> entityID;

    protected BukkitNametagTarget(LivingEntity entity) {
        super(entity::getUniqueId, entity::getLocation, entity::isValid);
        this.entityID = entity::getEntityId;
    }

    @Override
    public int getEntityID() {
        return entityID.get();
    }

}
