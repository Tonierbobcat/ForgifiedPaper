package com.loficostudios.forgified.paper.items;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import net.kyori.adventure.text.Component;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


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

    @SuppressWarnings("UnstableApiUsage")
    public static class Properties {
        private final Map<NamespacedKey, Consumer<ItemStack>> properties;
        private final List<Consumer<ItemStack>> custom;

        public Properties() {
             properties = new LinkedHashMap<>();
             custom = new ArrayList<>();
        }

        public Properties durability(int i) {
            properties.put(DataComponentTypes.MAX_DAMAGE.getKey(), (item) -> {
                item.setData(DataComponentTypes.MAX_DAMAGE, i);
            });
            return this;
        }

        public Properties food(FoodProperties foodProperties) {
            properties.put(DataComponentTypes.FOOD.getKey(), (item) -> {
                item.setData(DataComponentTypes.FOOD, foodProperties);
            });
            return this;
        }

        public Properties tool(Tool tool) {
            properties.put(DataComponentTypes.TOOL.getKey(), (item) -> {
                item.setData(DataComponentTypes.TOOL, tool);
            });
            return this;
        }

        public Properties model(CustomModelData model) {
            properties.put(DataComponentTypes.CUSTOM_MODEL_DATA.getKey(), (item) -> {
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, model);
            });
            return this;
        }

        public Properties custom(Consumer<ItemStack> property) {
            custom.add(property);
            return this;
        }

        public void apply(ItemStack item) {
            for (Consumer<ItemStack> property : properties.values()) {
                try {
                    property.accept(item);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Could not apply item property. " + e.getMessage());
                }
            }
            for (Consumer<ItemStack> property : custom) {
                try {
                    property.accept(item);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Could not apply item property. " + e.getMessage());
                }
            }
        }

        public static Properties empty() {
            return new Properties();
        }
    }
}
