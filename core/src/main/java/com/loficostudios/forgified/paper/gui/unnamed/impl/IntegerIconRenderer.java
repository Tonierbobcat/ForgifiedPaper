package com.loficostudios.forgified.paper.gui.unnamed.impl;

import com.loficostudios.forgified.paper.gui.GuiIcon;
import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.gui.unnamed.Property;
import com.loficostudios.forgified.paper.gui.unnamed.PropertyIconRenderer;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import com.loficostudios.forgified.paper.utils.EditRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class IntegerIconRenderer implements PropertyIconRenderer<Integer> {

    @Override
    public GuiIcon icon(Property<Integer> property) {
        var current = property.get();
        var display = Component.text(property.name())
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false);

        List<String> result = new ArrayList<>();
        result.add(PropertyIconRenderer.CURRENT_VALUE_TEXT + PropertyIconRenderer.formatCurrentValue(current));
        result.addAll(PropertyIconRenderer.getClickInstructions(property.mutable(), property.nullable(), false));

        var ico = GuiIcon.material(Material.NAME_TAG)
                .display(display)
                .description(result.stream().map(Component::text).toList());

        if (!property.mutable())
            return ico;

        ico = ico.onClick((p, c) -> {
            var gui = GuiManager.getInstance().getGui(p);
            switch (c) {
                case LEFT -> {

                    var request = new EditRequest.Builder()
                            .gui(gui)
                            .entryMessage(Component.text("Type in chat"))
                            .invalidValueMessage(Component.text("Invalid value"))
                            .callback((str) -> {
                                try {
                                    var integer = Integer.parseInt(str);
                                    property.set(integer);
                                    p.sendMessage(Component.text("Set " + property.name() + " to " + str));
                                } catch (NumberFormatException ignored) {
                                }
                            })
                            .valid((str) -> {
                                try {
                                    Integer.parseInt(str);
                                    return true;
                                } catch (NumberFormatException ignored) {
                                    return false;
                                }
                            });

                    ChatEditQueueManager.queueEdit(p, request.build());
                }
                case RIGHT -> {
                    if (!property.nullable())
                        return;
                    property.set(null);
                    gui.open(p);
                }
            }
        });

        return ico;
    }
}
