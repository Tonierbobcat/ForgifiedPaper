package com.loficostudios.forgified.paper.gui.unnamed;

import com.loficostudios.forgified.paper.gui.GuiIcon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Experimental
@FunctionalInterface
public interface PropertyIconRenderer<T> {
    String CURRENT_VALUE_TEXT = "§7Current Value: ";

    GuiIcon icon(Property<T> property);

    static String formatCurrentValue(@Nullable Object value) {
        String empty;

        if (value instanceof Number || value instanceof Enum<?> || value instanceof Boolean) {
            empty = "§a---";
        } else {
            empty = "§cNone";
        }

        if (value == null)
            return empty;

        String asString = value.toString();
        if (value instanceof Boolean bool) {
            asString = !bool ? "§cFALSE" : "§caTRUE";
        }

        return asString;
    }

    static List<String> getClickInstructions(boolean mutable, boolean nullable, boolean isSlider) {
        if (!mutable) return List.of();

        List<String> instructions = new ArrayList<>();
        instructions.add(" ");

        if (isSlider) {
            instructions.add("§e▶ Left click to go up");
            instructions.add("§e▶ Shift left click to go down");
        } else {
            instructions.add("§e▶ Left click to change value");
        }

        if (nullable) instructions.add("§e▶ Right click to unset");

        return instructions;
    }
}
