package com.loficostudios.forgified.paper.gui.unnamed;


import com.loficostudios.forgified.paper.UnnamedModule0;
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
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EnumIconRenderer<T extends Enum<T>> implements PropertyIconRenderer<T> {
    private final boolean slider;
    private final Map<T, Function<Property<T>, ItemStack>> icons;

    public EnumIconRenderer(boolean slider, Map<T, Function<Property<T>, ItemStack>> icons) {
        this.slider = slider;
        this.icons = icons;
    }

    @Override
    public GuiIcon icon(Property<T> property) {
        var current = property.get();
        ItemStack item = icons.getOrDefault(current, (e) -> new ItemStack(Material.BARRIER))
                .apply(property);

        List<String> result = new ArrayList<>();
        T[] values = (T[]) property.get().getClass().getEnumConstants();
        for (T value : values) {
            result.add((value.equals(current) ? "§7▶ {value}" : "§8  {value}")
                    .replace("{value}", value.name()));
        }

        BiConsumer<Player, ClickType> onClick = (p, c) -> {
            if (!c.isLeftClick())
                return;
            if (!slider) {
                var last = GuiManager.getInstance().getGui(p);
                last.close(p);
                UnnamedModule0.queueEdit(p, last, (s) -> {
                    for (T value : values) {
                        if (value.name().equals(s.toUpperCase())) {
                            property.set(value);
                            p.sendMessage("Set Lang to " + value);
                            last.open(p);
                            return;
                        }
                    }
                    throw new RuntimeException("Invalid argument");
                });
            } else {
                var gui = GuiManager.getInstance().getGui(p);
                var next = values[(current.ordinal() + 1) % values.length];
                property.set(next);
                gui.open(p);
            }
        };

        var display =  Component.text(property.name()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
        var description = result.stream().map(Component::text).toList();

        var icon = GuiIcon.item(item)
                .display(display)
                .description(description);

        return property.mutable() ? icon.onClick(onClick) : icon;
    }
}
