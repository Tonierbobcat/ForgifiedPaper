package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.items.ItemListener;
import com.loficostudios.forgified.paper.items.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgifiedPaper extends JavaPlugin {
    public static final String NAMESPACE = "forgifiedpaper";

    /// DEBUG
    public static final List<ItemRegistry> registries = new ArrayList<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new GuiManager(this), this);
        Bukkit.getPluginManager().registerEvents(new UnnamedModule0(this), this);
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
