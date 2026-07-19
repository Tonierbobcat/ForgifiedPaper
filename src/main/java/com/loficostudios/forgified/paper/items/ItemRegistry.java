package com.loficostudios.forgified.paper.items;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import com.loficostudios.forgified.paper.IPluginResources;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

// TODO add method for registering an item with JItem directly
// TODO maybe?? register(JItem item) {var id = item.getId(); ...}
/**
 * Registry for JItem.
 * This is responsible for loading item data from json files and providing methods to create JItem instances.
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemRegistry implements DeferredRegistry<JItem> {

    private final List<BiConsumer<JItem, ItemStack>> itemOverrides;

    /// store itemKey as static for utility methods
    private String namespace;

    private final Map<NamespacedKey, JItem> registered = new HashMap<>();

    public ItemRegistry() {
        this(List.of());
    }

    public ItemRegistry(List<BiConsumer<JItem, ItemStack>> itemOverrides) {
        this.itemOverrides = itemOverrides;
    }

    public void initialize(IPluginResources resources) {
        namespace = resources.namespace();

        /// DEBUG
        ForgifiedPaper.registries.add(this);

    }

    public JItem create(String id, Supplier<JItem> item) {
        var i = item.get();
        var namespacedKey = new NamespacedKey(namespace, id);
        try {
            var field = JItem.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(i, namespacedKey.asString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        registered.put(namespacedKey, i);
        return i;
    }

    public ItemStack createItemStack(JItem item) {
        return new ItemStackGenerator(itemOverrides).generate(item);
    }

    private void clearData(ItemStack item) {
        Field[] fields = DataComponentTypes.class.getDeclaredFields();

        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            try {
                Object value = field.get(null);



                try {
                    item.unsetData(((DataComponentType) value));
                } catch (Exception e) {
                    Bukkit.getLogger().severe(e.getMessage());
                }
            } catch (IllegalAccessException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        }
    }

    public ItemStack createItemStack(JItem item, int amount) {
        var i = createItemStack(item);
        i.setAmount(amount);
        return i;
    }

    /**
     *
     * @return An unmodifiable collection of registered items
     */
    public Collection<JItem> getRegistered() {
        return Collections.unmodifiableCollection(registered.values());
    }

    public JItem getById(String id) {
        return registered.get(new NamespacedKey(namespace, id));
    }

    public JItem getById(NamespacedKey id) {
        return registered.get(id);
    }

    public static @Nullable NamespacedKey getItemID(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || !item.hasItemMeta())
            return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        var id = pdc.get(ItemStackGenerator.ID_PATH, PersistentDataType.STRING);
        if (id == null)
            return null;
        return NamespacedKey.fromString(id);
    }

//    public NamespacedKey getItemKey() {
//        return itemKey;
//    }

    @Override
    public void register(IPluginResources resources) {
        this.initialize(resources);
    }
}
