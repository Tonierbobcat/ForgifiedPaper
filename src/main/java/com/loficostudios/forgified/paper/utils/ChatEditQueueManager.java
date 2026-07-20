package com.loficostudios.forgified.paper.utils;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import com.loficostudios.forgified.paper.ForgifiedPaperPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatEditQueueManager implements Listener {

    private final Map<UUID, EditRequest> queued = new ConcurrentHashMap<>();
    private final ForgifiedPaperPlugin plugin;

    public ChatEditQueueManager(ForgifiedPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isQueued(Player player) {
        return ForgifiedPaper.getChatEditQueueManager().queued.containsKey(player.getUniqueId());
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

                queued.put(player.getUniqueId(), request);

                return;
            }

            request.getCallback().accept(msg);

            if (request.getGUI() != null)
                request.getGUI().open(player);
        });
    }

    public void queue(Player player, EditRequest request) {
        queued.put(player.getUniqueId(), request);
        player.closeInventory();
        if (request.getEntryMessage() != null)
            player.sendMessage(request.getEntryMessage());
    }

    @Deprecated
    public static void queueEdit(Player player, EditRequest request) {
        ForgifiedPaper.getChatEditQueueManager().queue(player, request);
    }
}

