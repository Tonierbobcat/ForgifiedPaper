package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.items.JItem;
import com.loficostudios.forgified.paper.nametags.NametagManager;
import com.loficostudios.forgified.paper.items.ItemListener;
import com.loficostudios.forgified.paper.items.ItemRegistry;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import com.loficostudios.forgified.paper.utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ForgifiedPaperPlugin {
    NametagManager getNametagManager();

    GuiManager getGuiManager();

    ChatEditQueueManager getChatEditQueueManager();

    BukkitTask runTaskTimer(Runnable runnable, long delay, long ticks);

    BukkitTask runTaskTimer(Consumer<BukkitRunnable> runnable, long delay, long ticks);

    BukkitTask runTask(Runnable runnable);

    BukkitTask runTaskLater(Runnable runnable, long delay);

    BukkitTask runTaskAsynchronously(Runnable runnable);
}
