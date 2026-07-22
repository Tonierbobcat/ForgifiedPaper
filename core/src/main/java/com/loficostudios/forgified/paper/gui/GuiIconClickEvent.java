package com.loficostudios.forgified.paper.gui;

import org.bukkit.entity.Player;


public class GuiIconClickEvent extends GuiEvent {
    private final GuiIcon icon;
    public GuiIconClickEvent(Player player, FloralGui gui, GuiIcon icon) {
        super(player, gui);
        this.icon = icon;
    }

    public GuiIcon getIcon() {
        return icon;
    }
}
