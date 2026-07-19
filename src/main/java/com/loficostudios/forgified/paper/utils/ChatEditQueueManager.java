package com.loficostudios.forgified.paper.utils;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatEditQueueManager implements Listener {

    private final Map<UUID, EditRequest> queued = new ConcurrentHashMap<>();
    private final ForgifiedPaper plugin;

    private static ChatEditQueueManager instance;

    public ChatEditQueueManager(ForgifiedPaper plugin) {
        this.plugin = plugin;
        Validate.isTrue(instance == null);
        instance = this;
    }

    public static boolean isQueued(Player player) {
        return instance.queued.containsKey(player.getUniqueId());
    }

    @EventHandler
    private void onChat(AsyncChatEvent e) {
        var request = queued.remove(e.getPlayer().getUniqueId());
        if (request == null)
            return;
        e.setCancelled(true);

        var msg = LegacyComponentSerializer.legacyAmpersand().serialize(e.message());
        var player = e.getPlayer();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!request.validate(msg)) {

                if (request.getInvalidValueMessage() != null)
                    player.sendMessage(request.getInvalidValueMessage());

                instance.queued.put(player.getUniqueId(), request);

                return;
            }

            request.getCallback().accept(msg);

            if (request.getGUI() != null)
                request.getGUI().open(player);
        });
    }

    public static void queueEdit(Player player, EditRequest request) {
        instance.queued.put(player.getUniqueId(), request);
        player.closeInventory();
        if (request.getEntryMessage() != null)
            player.sendMessage(request.getEntryMessage());
    }
}

