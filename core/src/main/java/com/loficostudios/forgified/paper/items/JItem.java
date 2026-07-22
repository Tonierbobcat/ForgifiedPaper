package com.loficostudios.forgified.paper.items;

import com.loficostudios.forgified.paper.items.properties.Consumable;
import com.loficostudios.forgified.paper.utils.VersionHandler;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class JItem {
    private final Material baseMaterial;
    private final Properties properties;

    /// Keep this non final so it can be changed through reflection
    private String id;

    public JItem(Material baseMaterial, Properties properties) {
        Validate.isTrue(baseMaterial != null);
        Validate.isTrue(properties != null);

        this.properties = properties;
        this.baseMaterial = baseMaterial;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public String getId() {
        return id;
    }

    public Properties getProperties() {
        return properties;
    }

    public interface Properties {
        <T extends JItem> JItem.Properties custom(Class<T> clazz, BiConsumer<ItemStack, T> property);

        Properties durability(int i);

        JItem.Properties consumable(Consumable consumable);

//        JItem.Properties tool(Tool tool);

        JItem.Properties stackTo(int i);

        JItem.Properties model(String model);


        JItem.Properties custom(Consumer<ItemStack> property);

        void apply(ItemStack item, JItem i);

        static @NotNull Properties empty() {
            return null;
        }
    }
}
