package com.loficostudios.forgified.paper.gui.unnamed;

import com.loficostudios.forgified.paper.UnnamedModule0;
import com.loficostudios.forgified.paper.gui.GuiIcon;
import com.loficostudios.forgified.paper.gui.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PropertyIconFactory {
    private final Map<Class<?>, PropertyIconRenderer<?>> renderers = new HashMap<>();

    public PropertyIconFactory() {
        renderer(String.class, (p -> stringPropertyIcon(p)));
        renderer(Integer.class, p -> intPropertyIcon(p));
    }

    public <T> PropertyIconFactory renderer(Class<T> clazz, PropertyIconRenderer<T> renderer) {
        renderers.put(clazz, renderer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public @NotNull GuiIcon create(Property<?> property) {
        var clazz = property.clazz();
        var renderer = (PropertyIconRenderer<Object>) renderers.get(clazz);
        if (renderer == null) {
            return createDefaultIcon(property);
        }
        return renderer.icon((Property<Object>) property);
    }

    private GuiIcon intPropertyIcon(Property<Integer> property) {
        return createDefaultIcon(property,Material.HEAVY_CORE, (p) -> {
            var last = GuiManager.getInstance().getGui(p);
            UnnamedModule0.queueEdit(p, last, (s) -> {
                try {
                    var integer = Integer.parseInt(s);
                    property.set(integer);
                    p.sendMessage(Component.text("Set " + property.name() + " to " + s));
                } catch (NumberFormatException e) {
                    p.sendMessage(s + " is not an integer!");
                }
            });
        });
    }

    private GuiIcon stringPropertyIcon(Property<String> property) {
        return createDefaultIcon(property,Material.NAME_TAG, (p) -> {
            var last = GuiManager.getInstance().getGui(p);
            UnnamedModule0.queueEdit(p, last, (s) -> {
                property.set(s);
                p.sendMessage(Component.text("Set " + property.name() + " to " + s));
            });
        });
    }

    private GuiIcon createDefaultIcon(Property<?> property) {
        return createDefaultIcon(property, Material.BARRIER, null);
    }

    private GuiIcon createDefaultIcon(Property<?> property, Material icon, Consumer<Player> open) {
        var ico = GuiIcon.material(icon)
                .display(Component.text(property.name()).decoration(TextDecoration.ITALIC, false));

        ico = ico.description(List.of(Component.text(property.clazz().getSimpleName()), Component.text("Current: " + property.get())));
        if (!property.mutable())
            return ico;
        ico = ico.onClick((p, c) -> {
            if (c.isLeftClick() && open != null) {
                /// more like how does the value get edited
                open.accept(p);
            } else if (c.isRightClick()) {
                property.set(null);
            }
        });
        return ico;
    }
}
