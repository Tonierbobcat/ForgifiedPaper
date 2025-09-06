package com.loficostudios.forgified.paper.items;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.loficostudios.forgified.paper.IPluginResources;
import com.loficostudios.forgified.paper.utils.ResourceLoadingUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

// TODO add method for registering an item with JItem directly
// TODO maybe?? register(JItem item) {var id = item.getId(); ...}
/**
 * Registry for JItem.
 * This is responsible for loading item data from json files and providing methods to create JItem instances.
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemRegistry {

    private static final String ITEMS_FOLDER = "assets/items";

    /// store itemKey as static for utility methods
    private NamespacedKey itemKey;

    private final Map<String, JItem> registered = new HashMap<>();

    private final Map<String, Map<String, Object>> itemsMap = new HashMap<>();

    public void initialize(IPluginResources resources) {
        itemKey = new NamespacedKey(resources.namespace(), "items");

        ResourceLoadingUtils.extractDataFolderAndUpdate(resources, ItemRegistry.ITEMS_FOLDER, (file) -> {
            /// Load items.json
            /// which has data for the model and material
            var id = file.getName().replace(".json", "");
            loadJson(id, file);
        });
    }

    private void loadJson(String id, File json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        try (FileReader reader = new FileReader(json)) {
            itemsMap.put(id, gson.fromJson(reader, type));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load item: " + id + ".json", e);
        }
    }

    /// Create the item with an itemstack consumer for adding components
    public JItem create(String id, Consumer<ItemStack> item) {
        Validate.isTrue(!registered.containsKey(id), "Item with id " + id + " is already registered");
        var i = new JItem(id, (jitem) -> {
            var stack = itemStackFromJItem(jitem);
            item.accept(stack);
            return stack;
        });
        registered.put(id, i);
        return i;
    }

    public JItem create(String id) {
        Validate.isTrue(!registered.containsKey(id), "Item with id " + id + " is already registered");
        var i = new JItem(id, this::itemStackFromJItem);
        registered.put(id, i);
        return i;
    }

    private Component getName(String id) {
        /// DO NOT CHANGE THIS
        boolean isGeyser = true;

        /// Because this server is crossplay with bedrock we need to use non-translatable names
        Component component;
        if (isGeyser) {
            component = getGeyserCompatibleName(id);
        } else {
            component = Component.translatable("item." + itemKey.namespace() + "." + id);
        }
        return component.decoration(TextDecoration.ITALIC, false);
    }

    private Component getGeyserCompatibleName(String id) {
        var builder = new StringBuilder();

        var strings = id.split("_");

        for (String string : strings) {
            var chars = string.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            builder.append(chars).append(" ");
        }

        return Component.text(builder.toString().trim());
    }

    private ItemStack itemStackFromJItem(JItem item) {
        var id = item.getId();

        var itemStack = new ItemStack(getMaterial(item));
        var meta = itemStack.getItemMeta();
        assert meta != null;

        meta.displayName(getName(id));

        /// custom model data will work on geyser
        /// however the resource pack must be converted to bedrock first

        // check if model data exists
        var model = getModel(item);
        if (model != -1) {
            meta.getCustomModelDataComponent().setFloats(List.of((float)model));
        }

        var description = getDescription(item);
        System.out.println("Description for " + id + ": " + description);
        if (description != null && !description.isEmpty()) {
            var lore = new ArrayList<Component>();
            for (String line : description) {

                // Maybe parse this?
                lore.add(Component.text(line));
            }
            meta.lore(lore);
        }

        /// stores the item id in the itemstacks persistent data container
        setItemId(meta, item);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /// get the custom model data from map.
    /**
     *
     * @return -1 if no model data is found
     */
    private int getModel(JItem item) {
        var id = item.getId();
        Map<String, Object> data = itemsMap.get(id);
        if (data != null && data.get("model") instanceof Number num) {
            return num.intValue();
        }
        return -1;
    }

    private List<String> getDescription(JItem item) {
        var id = item.getId();
        Map<String, Object> data = itemsMap.get(id);
        if (data != null && data.get("description") instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof String str) {
                    result.add(str);
                }
            }
            return result;
        }
        return null;
    }

    /// get the custom model data from map. defaults to 0
    private Material getMaterial(JItem item) {
        var id = item.getId();
        Map<String, Object> data = itemsMap.get(id);
        if (data != null && data.get("material") instanceof String material) {
            return Material.getMaterial(material);
        }
        return Material.STONE;
    }

    private void setItemId(ItemMeta meta, JItem item) {
        var id = item.getId();
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, id);
    }

    /**
     *
     * @return An unmodifiable collection of registered items
     */
    public Collection<JItem> getRegistered() {
        return Collections.unmodifiableCollection(registered.values());
    }

    public JItem getById(String id) {
        return registered.get(id);
    }

    public NamespacedKey getItemKey() {
        return itemKey;
    }
}
