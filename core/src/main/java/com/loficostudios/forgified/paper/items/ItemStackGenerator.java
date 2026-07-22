package com.loficostudios.forgified.paper.items;

//import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemStackGenerator {
    public static final NamespacedKey ID_PATH = new NamespacedKey("forgifed_paper", "item_id");

//    private final NamespacedKey itemKey;
    private final List<BiConsumer<JItem, ItemStack>> itemOverrides;

    public ItemStackGenerator() {
//        this.itemKey = itemKey;
        itemOverrides = new ArrayList<>();
    }

    public ItemStackGenerator(List<BiConsumer<JItem, ItemStack>> itemOverrides) {
//        this.itemKey = itemKey;
        this.itemOverrides = itemOverrides;
    }

    public ItemStack generate(JItem item) {
//        var id = item.getId();

        Validate.isTrue(item.getId() != null, "Id cannot be null");

        var namespacedKey = NamespacedKey.fromString(item.getId());

        Validate.isTrue(namespacedKey != null, "namespacedKey is null");

        var stack = new ItemStack(item.getBaseMaterial());

        var meta = stack.getItemMeta();
        Validate.isTrue(meta != null, "Meta is null");

//        clearData(stack);

        meta.getPersistentDataContainer().set(ID_PATH, PersistentDataType.STRING, namespacedKey.toString());

        meta.displayName(Component.translatable("item." + namespacedKey.namespace() + "." + namespacedKey.value())
                .decoration(TextDecoration.ITALIC, false));

        /// Set ItemStack meta before applying properties
        stack.setItemMeta(meta);

        /// By default, set the model to the namespace:itemid
//        stack.setData(DataComponentTypes.ITEM_MODEL, namespacedKey);

        item.getProperties().apply(stack, item);

//        var model = stack.getData(DataComponentTypes.ITEM_MODEL);
//        Bukkit.getLogger().info("NAMESPACEKEY FOR ITEM IS " + (model != null ? model.asString() : "null"));

        for (BiConsumer<JItem, ItemStack> itemOverride : itemOverrides) {
            /// Surround in try and catch to prevent error
            try {
                itemOverride.accept(item, stack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stack;
    }
}
