package com.loficostudios.forgified.paper.utils;

import com.loficostudios.forgified.paper.items.ItemRegistry;
import com.loficostudios.forgified.paper.items.JItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class RegistryUtils {
    public static ItemStack getItemStack(ItemRegistry registry, JItem item) {
        Validate.isTrue(registry != null);
        Validate.isTrue(item != null);

        /// Check if item exists in the registry before trying to get
        Validate.isTrue(registry.getById(item.getId()) != null);

        net.minecraft.world.item.Item nmsItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("japaneseminecraft", item.getId())).orElseThrow().value();
        net.minecraft.world.item.ItemStack nmsStack = new net.minecraft.world.item.ItemStack(nmsItem);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
}
