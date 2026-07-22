package com.loficostudios.forgified.paper_1_20_6;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import com.loficostudios.forgified.paper.items.JItem;
import com.loficostudios.forgified.paper.items.properties.Consumable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class Properties1_20_6 implements JItem.Properties {
    private final Map<NamespacedKey, Consumer<ItemStack>> properties;
    private final List<BiConsumer<ItemStack, ? extends JItem>> custom;

    public Properties1_20_6() {
        this.properties = new HashMap<>();
        this.custom = new ArrayList<>();
    }

    @Override
    public <T extends JItem> JItem.Properties custom(Class<T> clazz, BiConsumer<ItemStack, T> property) {
        BiConsumer<ItemStack, ? extends JItem> wrapper = (stack, item) -> {
            if (clazz.isInstance(item)) {
                property.accept(stack, clazz.cast(item));
            }
        };

        custom.add(wrapper);
        return this;
    }

    @Override
    public JItem.Properties durability(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JItem.Properties consumable(Consumable consumable) {
        properties.put(new NamespacedKey(ForgifiedPaper.NAMESPACE, "food"), (item) -> {
            var meta = item.getItemMeta();
            var food = meta.getFood();

            for (Consumable.Effects effect : consumable.getEffects()) {
                food.addEffect(effect.effect(), effect.probability());
            }

            food.setNutrition(consumable.getNutrition());
            food.setSaturation(consumable.getSaturation());

            meta.setFood(food);
        });
        return this;
    }

    @Override
    public JItem.Properties stackTo(int i) {
        properties.put(new NamespacedKey(ForgifiedPaper.NAMESPACE, "stackTo"), (item) -> {
            var meta = item.getItemMeta();
            meta.setMaxStackSize(i);
            item.setItemMeta(meta);
        });
        return this;
    }
    @Override
    public JItem.Properties model(String model) {
        properties.put(new NamespacedKey(ForgifiedPaper.NAMESPACE, "model"), (item) -> {
            var meta = item.getItemMeta();
            meta.setCustomModelData(Integer.valueOf(model));
            item.setItemMeta(meta);
        });
        return this;
    }

    @Override
    public JItem.Properties custom(Consumer<ItemStack> property) {
        custom.add((i, item) -> property.accept(i));
        return this;
    }

    @Override
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
}
