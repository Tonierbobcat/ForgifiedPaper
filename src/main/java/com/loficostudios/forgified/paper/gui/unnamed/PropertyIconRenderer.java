package com.loficostudios.forgified.paper.gui.unnamed;

import com.loficostudios.forgified.paper.gui.GuiIcon;

@FunctionalInterface
public interface PropertyIconRenderer<T> {
    GuiIcon icon(Property<T> property);
}
