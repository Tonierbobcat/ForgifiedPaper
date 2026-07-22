package com.loficostudios.forgified.paper.gui;

import net.kyori.adventure.text.Component;

import java.util.Optional;

public abstract class NavigableGui extends AbstractFloralGui implements Navigable {
    private final FloralGui parent;
    public NavigableGui(int size, FloralGui parent) {
        super(size);
        this.parent = parent;
    }

    public NavigableGui(int size, Component title, FloralGui parent) {
        super(size, title);
        this.parent = parent;
    }


    @Override
    public Optional<FloralGui> parent() {
        return Optional.ofNullable(parent);
    }
}
