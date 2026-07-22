package com.loficostudios.forgified.paper.gui;

import org.bukkit.entity.Player;

public class GuiCloseEvent extends GuiEvent {
    public GuiCloseEvent(Player player, FloralGui gui) {
        super(player, gui);
    }
}
