/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */

package com.loficostudios.forgified.paper.gui;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/// READ ONLY ICON
public class GuiIcon {

    private final ItemStack stack;
    private final BiConsumer<Player, ClickType> onClick;
    private final Component display;
    private final List<? extends Component> description;

    public static GuiIcon item(ItemStack item) {
        return new GuiIcon(item, item.displayName(), List.of(), null);
    }

    public static GuiIcon material(Material mat) {
        return item(ItemStack.of(mat));
    }

    protected GuiIcon(ItemStack item, Component display, List<? extends Component> description, @Nullable BiConsumer<Player, ClickType> onClick) {
        Validate.isTrue(item != null, "Item is null");
        Validate.isTrue(display != null, "Display is null");
        Validate.isTrue(description != null, "Description is null");

        var clone = item.clone();
        var meta = clone.getItemMeta();
        if (meta != null) {
            meta.displayName(display);

            /// overrides description
            if (!description.isEmpty()) {
                var lore = meta.lore();
                if (lore != null && !lore.isEmpty()) {
                    lore.add(Component.text(" "));
                    lore.addAll(description);
                } else {
                    lore = new ArrayList<>(description);
                }
                meta.lore(lore);
            } else {
                /// description is item description
                var lore = meta.lore();
                description = lore != null ? lore : List.of();
            }

            clone.setItemMeta(meta);
        }

        this.stack = clone;
        this.onClick = onClick;
        this.description = description;
        this.display = display;
    }

    protected GuiIcon(ItemStack item, Component display, List<? extends Component> description) {
        this(item, display, description, null);
    }

    protected GuiIcon(ItemStack item, Component display) {
        this(item, display, List.of(), null);
    }

    protected GuiIcon(Material material, Component display, List<? extends Component> description, @Nullable BiConsumer<Player, ClickType> onClick) {
        this(ItemStack.of(material), display, description, onClick);
    }

    protected GuiIcon(Material material, Component display, @Nullable BiConsumer<Player, ClickType> onClick) {
        this(material, display, List.of(), onClick);
    }

    protected GuiIcon(Material material, Component display) {
        this(material, display, List.of(), null);
    }

    private static Component getDisplayNameOrElseMaterialName(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        } else {
            return Component.text(formatEnumName(item.getType()));
        }
    }

    private static <T extends Enum<T>> String formatEnumName(Enum<T> tEnum) {
        return formatEnumName(tEnum.name());
    }

    private static String formatEnumName(String name) {
        Validate.isTrue(name != null);
        var builder = new StringBuilder();
        var strings = name.toLowerCase().split("_");
        for (String string : strings) {
            var chars = string.toCharArray();
            if (chars.length == 0)
                continue;
            chars[0] = Character.toUpperCase(chars[0]);
            builder.append(chars).append(" ");
        }
        return builder.toString().trim();
    }

    public Component display() {
        return display;
    }

    public GuiIcon display(Component display) {
        return new GuiIcon(stack, display, description, onClick);
    }

    public ItemStack item() {
        return stack.clone();
    }

    public GuiIcon amount(int amount) {
        var clone = stack.clone();
        clone.setAmount(amount);
        return new GuiIcon(clone, display, description, onClick);
    }

    public @NotNull List<Component> description() {
//        var meta = itemStack.getItemMeta();
//        if (meta != null) {
//            var lore = meta.lore();
//            return lore != null ? lore : List.of();
//        } else {
//            try {
//                throw new IllegalArgumentException("Meta is not found");
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
        return new ArrayList<>(description);
    }

    public GuiIcon description(@NotNull List<? extends Component> lines) {
//        var meta = stack.getItemMeta();
//        if (meta != null) {
//            meta.lore(lines);
//            stack.setItemMeta(meta);
//        } else {
//            try {
//                throw new IllegalArgumentException("Meta is not found");
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }

        return new GuiIcon(stack, display, lines, onClick);
    }

    public GuiIcon onClick(@Nullable BiConsumer<Player, ClickType> onClick) {
        return new GuiIcon(stack, display, description, onClick);
    }

    public GuiIcon onClick(@Nullable Consumer<Player> onClick) {
        return new GuiIcon(stack, display, description, onClick != null ? (p,c) -> onClick.accept(p) : null);
    }

    public void onClick(InventoryClickEvent e) {
        Bukkit.getLogger().info("action: " + (onClick == null ? "null" : "present") + " onClick: player: " + e.getWhoClicked().getName() +  " click: " +  e.getClick() );
        if (this.onClick == null)
            return;
        onClick.accept(((Player) e.getWhoClicked()), e.getClick());
    }
}