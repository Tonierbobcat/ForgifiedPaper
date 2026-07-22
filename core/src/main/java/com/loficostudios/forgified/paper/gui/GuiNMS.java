package com.loficostudios.forgified.paper.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public interface GuiNMS {
    void updateTitle(List<HumanEntity> viewers, FloralGui gui, Component title);
}
