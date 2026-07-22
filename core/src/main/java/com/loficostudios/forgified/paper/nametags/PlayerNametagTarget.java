package com.loficostudios.forgified.paper.nametags;

import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class PlayerNametagTarget extends BukkitNametagTarget implements PlayerTarget {
    private final Supplier<Boolean> isOnline;

    protected PlayerNametagTarget(Player entity) {
        super(entity);
        this.isOnline = entity::isOnline;
    }

    @Override
    public boolean isOnline() {
        return isOnline.get();
    }
}
