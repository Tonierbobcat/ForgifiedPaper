package com.loficostudios.forgified.paper.gui.unnamed;

import com.loficostudios.forgified.paper.gui.FloralGui;
import com.loficostudios.forgified.paper.gui.unnamed.impl.BooleanIconRenderer;
import com.loficostudios.forgified.paper.gui.unnamed.impl.IntegerIconRenderer;
import com.loficostudios.forgified.paper.gui.unnamed.impl.StringIconRenderer;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import com.loficostudios.forgified.paper.gui.GuiIcon;
import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.utils.EditRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Experimental
public class PropertyIconFactory {
    private final Map<Class<?>, PropertyIconRenderer<?>> renderers = new HashMap<>();

    public PropertyIconFactory() {
        renderer(Boolean.class, (p) -> new BooleanIconRenderer().icon(p));
        renderer(String.class, (p -> new StringIconRenderer().icon(p)));
        renderer(Integer.class, p -> new IntegerIconRenderer().icon(p));
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
