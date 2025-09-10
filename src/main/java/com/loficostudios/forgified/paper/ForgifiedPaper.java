package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.items.ItemListener;
import com.loficostudios.forgified.paper.items.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ForgifiedPaper extends JavaPlugin {
    public static final String NAMESPACE = "forgifiedpaper";

    /// DEBUG
    public static final List<ItemRegistry> registries = new ArrayList<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
    }
}
