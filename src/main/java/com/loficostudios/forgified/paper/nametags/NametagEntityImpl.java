package com.loficostudios.forgified.paper.nametags;

import org.bukkit.Location;

import java.util.UUID;

public class NametagEntityImpl implements NametagEntity {
    private final NametagTarget target;
    private final NameTag nameTag;

    protected NametagEntityImpl(NametagTarget target, NameTag nameTag) {
        this.target = target;
        this.nameTag = nameTag;
    }

    @Override
    public Location getAnchor() {
        return target.getLocation();
    }

    @Override
    public NameTag getNameTag() {
        return nameTag;
    }

    @Override
    public UUID getUID() {
        return target.getUID();
    }

    @Override
    public NametagTarget getTarget() {
        return target;
    }
}
