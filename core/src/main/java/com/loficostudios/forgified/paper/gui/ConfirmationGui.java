package com.loficostudios.forgified.paper.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class ConfirmationGui extends AbstractFloralGui implements Navigable {

    private final FloralGui parent;

    private final Consumer<Player> onYes;
    private final Consumer<Player> onNo;

    public ConfirmationGui(String message, FloralGui parent, Consumer<Player> onYes, Consumer<Player> onNo) {
        super(9, Component.text(message));
        this.onNo = onNo;
        this.parent = parent;
        this.onYes = onYes;

        setSlot(2, getYesButton());
        setSlot(6, getNoButton());
    }

    public ConfirmationGui(String message, FloralGui parent, Consumer<Player> onYes) {
        this(message, parent, onYes, null);
    }

    private GuiIcon getYesButton() {
        return GuiIcon.material(Material.LIME_STAINED_GLASS).display(MiniMessage.miniMessage().deserialize("<green><bold>Confirm")).onClick((p) -> {
            close(p);
            GuiManager.getInstance().getPlugin().runTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    if (onYes != null) {
                        onYes.accept(p);
                    }
                }
            }, 1);
        });
    }

    private GuiIcon getNoButton() {
        return GuiIcon.material(Material.RED_STAINED_GLASS).display(MiniMessage.miniMessage().deserialize("<red><bold>Cancel")).onClick((p -> {
            close(p);
            GuiManager.getInstance().getPlugin().runTaskLater(() -> {
                if (onNo != null) {
                    onNo.accept(p);
                }
            }, 1);
        }));
    }

    @Override
    public Optional<FloralGui> parent() {
        return Optional.ofNullable(parent);
    }
}
