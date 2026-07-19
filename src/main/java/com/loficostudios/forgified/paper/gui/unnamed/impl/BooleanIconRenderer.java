package com.loficostudios.forgified.paper.gui.unnamed.impl;

import com.loficostudios.forgified.paper.gui.GuiIcon;
import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.gui.unnamed.Property;
import com.loficostudios.forgified.paper.gui.unnamed.PropertyIconRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BooleanIconRenderer implements PropertyIconRenderer<Boolean> {
    @Override
    public GuiIcon icon(Property<Boolean> property) {
        var current = property.get();

        List<String> result = new ArrayList<>();

        result.add(PropertyIconRenderer.CURRENT_VALUE_TEXT + PropertyIconRenderer.formatCurrentValue(current));
        result.addAll(PropertyIconRenderer.getClickInstructions(property.mutable(), property.nullable(), false));

        BiConsumer<Player, ClickType> action = (p,c) -> {
            var gui = GuiManager.getInstance().getGui(p);
            switch (c) {
                case RIGHT -> {
                    if (!property.nullable())
                        return;
                    property.set(null);
                }
                case LEFT -> {
                    property.set(current == null || !current);
                    gui.open(p);
                }
            }
        };

        var display =  Component.text(property.name()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        var description = result.stream().map(Component::text).toList();

        return GuiIcon.item(ItemStack.of(current != null ? (!current ? Material.MINECART : Material.CHEST_MINECART) : Material.MINECART))
                .onClick(action)
                .display(display)
                .description(description);
    }
}
