package com.loficostudios.forgified.paper.nametags;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameTag {
    private List<Component> lines;
    @NotNull Component cached;

    protected NameTag(List<Component> lines) {
        this.lines = new ArrayList<>(lines);
        cached = Component.join(JoinConfiguration.separator(Component.newline()), lines);
    }

    public List<Component> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void setLines(List<Component> lines) {
        this.lines = lines;
        cached = Component.join(JoinConfiguration.separator(Component.newline()), lines);
    }
}
