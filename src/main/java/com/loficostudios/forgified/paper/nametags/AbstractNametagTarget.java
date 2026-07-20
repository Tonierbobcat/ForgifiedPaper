package com.loficostudios.forgified.paper.nametags;

import org.bukkit.Location;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractNametagTarget implements NametagTarget {
    private final Supplier<UUID> uid;
    private final Supplier<Location> location;
    private final Supplier<Boolean> isValid;

    protected AbstractNametagTarget(Supplier<UUID> uid, Supplier<Location> location, Supplier<Boolean> isValid) {
        this.uid = uid;
        this.location = location;
        this.isValid = isValid;
    }

    @Override
    public UUID getUID() {
        return uid.get();
    }

    @Override
    public Location getLocation() {
        return location.get();
    }

    @Override
    public boolean isValid() {
        return isValid.get();
    }

}
