package com.loficostudios.forgified.plugin;

import com.loficostudios.forgified.paper.ForgifiedPaper;
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

public class ForgifiedPaperPlugin extends JavaPlugin implements com.loficostudios.forgified.paper.ForgifiedPaperPlugin {


    /// DEBUG
    public static final List<ItemRegistry> registries = new ArrayList<>();

    private NametagManager nametagManager;
    private GuiManager guiManager;
    private ChatEditQueueManager chatEditQueueManager;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        ForgifiedPaper.init(this);

        this.nametagManager = new NametagManager(this);
        this.guiManager = new GuiManager(this);
        this.chatEditQueueManager = new ChatEditQueueManager(this);

        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);

        Bukkit.getPluginManager().registerEvents(guiManager, this);
        Bukkit.getPluginManager().registerEvents(nametagManager, this);
        Bukkit.getPluginManager().registerEvents(chatEditQueueManager, this);
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public ChatEditQueueManager getChatEditQueueManager() {
        return chatEditQueueManager;
    }

    public BukkitTask runTaskTimer(Runnable runnable, long delay, long ticks) {
        if (runnable instanceof BukkitRunnable) {
            return ((BukkitRunnable) runnable).runTaskTimer(this, delay, ticks);
        } else {
            return this.getServer().getScheduler().runTaskTimer(this, runnable, delay, ticks);
        }
    }

    public BukkitTask runTaskTimer(Consumer<BukkitRunnable> runnable, long delay, long ticks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.accept(this);
            }
        }.runTaskTimer(this, delay, ticks);
    }

    public BukkitTask runTask(Runnable runnable) {
        if (runnable instanceof BukkitRunnable) {
            return ((BukkitRunnable) runnable).runTask(this);
        } else {
            return this.getServer().getScheduler().runTask(this, runnable);
        }
    }

    public BukkitTask runTaskLater(Runnable runnable , long delay) {
        if (runnable instanceof BukkitRunnable) {
            return ((BukkitRunnable) runnable).runTaskLater(this, delay);
        } else {
            return this.getServer().getScheduler().runTaskLater(this, runnable, delay);
        }
    }

    public BukkitTask runTaskAsynchronously(Runnable runnable) {
        if (runnable instanceof BukkitRunnable) {
            return ((BukkitRunnable) runnable).runTaskAsynchronously(this);
        } else {
            return this.getServer().getScheduler().runTaskAsynchronously(this, runnable);
        }
    }
}
