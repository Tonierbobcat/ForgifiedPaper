package com.loficostudios.forgified.paper.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.IntRange;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @SuppressWarnings("UnstableApiUsage")
    public static class Properties {
        private final Map<NamespacedKey, Consumer<ItemStack>> properties;
        private final List<BiConsumer<ItemStack, ? extends JItem>> custom;

        public <T extends JItem> Properties custom(Class<T> clazz, BiConsumer<ItemStack, T> property) {
            BiConsumer<ItemStack, ? extends JItem> wrapper = (stack, item) -> {
                if (clazz.isInstance(item)) {
                    property.accept(stack, clazz.cast(item));
                }
            };
            custom.add(wrapper);
            return this;
        }

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

        public Properties stackTo(@IntRange(from = 1, to = 99) Integer i) {
            properties.put(DataComponentTypes.MAX_STACK_SIZE.getKey(), (item) -> {
                item.setData(DataComponentTypes.MAX_STACK_SIZE, i);
            });
            return this;
        }

        /**
         * Overrides model with material texture
         */
        public Properties model() {
            properties.put(DataComponentTypes.ITEM_MODEL.getKey(), (item) -> {
                var mat = item.getType().name().toLowerCase();
                item.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(mat));
            });
            return this;
        }

        public Properties custom(Consumer<ItemStack> property) {
            custom.add((i, item) -> property.accept(i));
            return this;
        }

        @SuppressWarnings("unchecked")
        public void apply(ItemStack item, JItem i) {
            for (Consumer<ItemStack> property : properties.values()) {
                try {
                    property.accept(item);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Could not apply item property. " + e.getMessage());
                }
            }
            for (BiConsumer<ItemStack, ? extends JItem> property : custom) {
                try {

                    ((BiConsumer<ItemStack, JItem>) property).accept(item, i);
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
