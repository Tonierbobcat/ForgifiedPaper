package com.loficostudios.forgified.paper.nametags;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NameTagNMS_1_19_4 implements NameTagNMS {
    private static final double LINE_SPACING = 0.25;

    private final Map<NametagEntity, int[]> passengers = new HashMap<>();

    protected NameTagNMS_1_19_4() {
    }

    @Override
    public void remove(NametagEntity entity) {
        var existing = passengers.get(entity);
        if (existing == null) {
            return;
        }
        var packet = new WrapperPlayServerDestroyEntities(existing);
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
        this.passengers.remove(entity);
    }

    private List<EntityData<?>> getEntityData(NametagEntity entity) {
         return List.of(
                new EntityData<>(
                        12,
                        EntityDataTypes.VECTOR3F,
                        new Vector3f(1,1,1)
                ),
                new EntityData<>(
                        11,
                        EntityDataTypes.VECTOR3F,
                        new Vector3f(0, (float) 0.25, 0)
                ),
                new EntityData<>(
                        15,
                        EntityDataTypes.BYTE,
                        (byte) 3
                ),
                new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, entity.getNameTag().cached)
        );
    }

    @Override
    public void update(NametagEntity entity) {
        var existing = passengers.get(entity);
        if (existing == null || existing.length == 0) {
            return;
        }
        var entityId = existing[0];

        var metadataPacket = new WrapperPlayServerEntityMetadata(entityId, getEntityData(entity));

        for (Player player : Bukkit.getOnlinePlayers()) {
            var pm = PacketEvents.getAPI().getPlayerManager();
            pm.sendPacket(player, metadataPacket);
        }
    }

    @Override
    public void create(NametagEntity entity) {
        int entityId = SpigotReflectionUtil.generateEntityId();
        UUID uuid = UUID.randomUUID();
        Location baseLoc = entity.getAnchor();
        var spawnPacket = new WrapperPlayServerSpawnEntity(entityId, uuid,
                EntityTypes.TEXT_DISPLAY,
                SpigotConversionUtil.fromBukkitLocation(baseLoc),
                0,
                0,
                null);

        var metadataPacket = new WrapperPlayServerEntityMetadata(entityId, getEntityData(entity));

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadataPacket);
        }

        int[] passengers = new int[]{ entityId };

        if (entity.getTarget() instanceof BukkitTarget bukkit) {
            var mountPacket = new WrapperPlayServerSetPassengers(bukkit.getEntityID(), passengers);
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, mountPacket);
            }
        }

        this.passengers.put(entity, passengers);
    }
}
