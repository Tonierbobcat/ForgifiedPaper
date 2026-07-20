package com.loficostudios.forgified.paper.nametags;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@ApiStatus.Internal
public interface NametagEntity {
    Location getAnchor();
    NameTag getNameTag();
    UUID getUID();
    NametagTarget getTarget();
}