package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.gui.FloralGui;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UnnamedModule0 implements Listener {

    private final Map<UUID,A> queued = new ConcurrentHashMap<>();
    private final ForgifiedPaper plugin;

    private static UnnamedModule0 instance;

    public UnnamedModule0(ForgifiedPaper plugin) {
        this.plugin = plugin;
        Validate.isTrue(instance == null);
        instance = this;
    }

    @EventHandler
    private void onChat(AsyncChatEvent e) {
        var a = queued.remove(e.getPlayer().getUniqueId());
        if (a == null)
            return;
        e.setCancelled(true);

        var msg = LegacyComponentSerializer.legacyAmpersand().serialize(e.message());
        new BukkitRunnable() {
            @Override
            public void run() {
                a.callback.accept(msg);
                a.gui().open(e.getPlayer());
            }
        }.runTask(plugin);
    }

    private record A(Player player, FloralGui gui, Consumer<String> callback) {
    }

    public static void queueEdit(Player player, FloralGui gui, Consumer<String> callback) {
        instance.queued.put(player.getUniqueId(), new A(player, gui, callback));
        player.closeInventory();
        player.sendMessage("Type value in chat");
    }

    public static void queueEdit(Player player, FloralGui gui, String message, Consumer<String> callback) {
        instance.queued.put(player.getUniqueId(), new A(player, gui, callback));
        player.closeInventory();
        player.sendMessage(message);
    }
}
