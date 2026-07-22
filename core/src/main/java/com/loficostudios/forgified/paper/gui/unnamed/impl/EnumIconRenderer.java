package com.loficostudios.forgified.paper.gui.unnamed.impl;


import com.loficostudios.forgified.paper.gui.FloralGui;
import com.loficostudios.forgified.paper.gui.unnamed.Property;
import com.loficostudios.forgified.paper.gui.unnamed.PropertyIconRenderer;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import com.loficostudios.forgified.paper.gui.GuiIcon;
import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.utils.EditRequest;
import com.loficostudios.forgified.paper.utils.PaginatedCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@ApiStatus.Experimental
public class EnumIconRenderer<T extends Enum<T>> implements PropertyIconRenderer<T> {
    private final boolean slider;
    private final Map<T, Function<Property<T>, ItemStack>> icons;
    private final int perPage;

    public EnumIconRenderer(boolean slider, Map<T, Function<Property<T>, ItemStack>> icons, int perPage) {
        this.slider = slider;
        this.icons = icons;
        this.perPage = perPage;
    }

    private List<String> getPaginatedDescription(@Nullable T current, T[] values, boolean mutable, boolean nullable) {
        List<String> result = new ArrayList<>();

        var collection = new PaginatedCollection<>(values, perPage);

        int page = current != null ? collection.getPageOf(current) : 1;
        var pageValues = collection.getPage(page);

        result.add(PropertyIconRenderer.CURRENT_VALUE_TEXT + PropertyIconRenderer.formatCurrentValue(current));

        result.add("§7Page ({current}/{max})"
                .replace("{current}", "" +page)
                .replace("{max}", "" + collection.getMaxPage()));
        for (T value : pageValues) {
            result.add((value.equals(current) ? "§7▶ {value}" : "§8  {value}")
                    .replace("{value}", value.name()));
        }

        result.addAll(PropertyIconRenderer.getClickInstructions(mutable, nullable, slider));
        return result;
    }

    private List<String> getNonePaginatedDescription(@Nullable T current, T[] values, boolean mutable, boolean nullable) {
        List<String> result = new ArrayList<>();
        result.add(PropertyIconRenderer.CURRENT_VALUE_TEXT + PropertyIconRenderer.formatCurrentValue(current));
        for (T value : values) {
            result.add((value.equals(current) ? "§7▶ {value}" : "§8  {value}")
                    .replace("{value}", value.name()));
        }

        result.addAll(PropertyIconRenderer.getClickInstructions(mutable, nullable, slider));
        return result;
    }

    @Override
    public GuiIcon icon(Property<T> property) {
        @Nullable T current = property.get();
        ItemStack item = icons.getOrDefault(current, (e) -> new ItemStack(Material.BARRIER))
                .apply(property);

        T[] values = property.clazz().getEnumConstants();

        List<String> result;
        if (values.length > perPage) {
            result = getPaginatedDescription(current, values, property.mutable(), property.nullable());
        } else {
            result = getNonePaginatedDescription(current, values, property.mutable(), property.nullable());
        }

        BiConsumer<Player, ClickType> onClick = (p, click) -> {
            var gui = GuiManager.getInstance().getGui(p);
            switch (click) {
                case RIGHT -> {
                    if (!property.nullable())
                        return;

                    property.set(null);
                    gui.open(p);
                }
                case LEFT -> {
                    if (!slider) {
                        requestChatEdit(p, gui, values, property);
                        return;
                    }

                    /// fallback if current is null
                    T target = values[0];

                    if (current != null)
                        target = values[(current.ordinal() + 1) % values.length];

                    property.set(target);
                    gui.open(p);

                }
                case SHIFT_LEFT -> {
                    /// fallback if current is null
                    T target = values[0];

                    if (current != null)
                        target = values[(current.ordinal() - 1 + values.length) % values.length];

                    property.set(target);
                    gui.open(p);
                }
            }
        };

        var display =  Component.text(property.name()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        var description = result.stream().map(Component::text).toList();

        var icon = GuiIcon.item(item)
                .display(display)
                .description(description);

        return property.mutable() ? icon.onClick(onClick) : icon;
    }

    private void requestChatEdit(Player player, FloralGui gui, T[] values, Property<T> property) {
        gui.close(player);

        var request = new EditRequest.Builder()
                .gui(gui)
                .entryMessage(Component.text("Type value in chat"))
                .valid((str) -> {
                    for (T value : values) {
                        if (value.name().equals(str.toUpperCase())) {
                            return true;
                        }
                    }
                    return false;
                })
                .invalidValueMessage(Component.text("Invalid value"))
                .callback((str) -> {
                    for (T value : values) {
                        if (value.name().equals(str.toUpperCase())) {
                            property.set(value);
                            gui.open(player);
                            return;
                        }
                    }
                }).build();

        ChatEditQueueManager.queueEdit(player, request);
    }
}
