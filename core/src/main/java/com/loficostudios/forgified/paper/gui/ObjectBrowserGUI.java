package com.loficostudios.forgified.paper.gui;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ObjectBrowserGUI<T> extends PaginatedGui implements Navigable {
    @Override
    protected void loadPage(Player player, int page) {
        var filtered = items.stream().filter(i -> {
            if (show == null)
                return true;
            return show.test(player, i);
        }).toList();

        var paginated = paginate(filtered, page, perPage);

        var index = 0;

        for (T item : paginated) {
            var ico = this.icon.apply(player, item);
            ico = ico.onClick((p,c) -> {
                onClick.accept(p,item,c);
//                var gui = ForgifiedPaper.getGuiManager().getGui(p);
//                gui.close(p);
            });
            setSlot(index, ico);
            index++;
        }

        int totalSlots = size.value();
        int previousPageSlot = totalSlots - 2;
        int nextPageSlot = totalSlots - 1;

        setSlot(previousPageSlot, GuiIcon.material(Material.RED_STAINED_GLASS_PANE)
                .display(Component.text("Previous Page"))
                .onClick(this::previousPage));

        setSlot(nextPageSlot, GuiIcon.material(Material.GREEN_STAINED_GLASS_PANE)
                .display(Component.text("Next Page"))
                .onClick(this::nextPage));

        int maxPages = (int) Math.ceil((double) filtered.size() / perPage);
        if (page > 0) {
            this.setTitle(Component.text("Items (Page " + (page + 1) + "/" + (maxPages) + ")"));
        } else {
            this.setTitle(Component.text("Items"));
        }
    }

    @Override
    protected int maxPage(Player player) {
        return maxPage(items, perPage);
    }

    @Override
    public Optional<FloralGui> parent() {
        return Optional.ofNullable(parent);
    }

    public enum BrowserSize {
        SIZE9X1(2*9),
        SIZE9X2(3*9),
        SIZE9X3(4*9),
        SIZE9X4(5*9),
        SIZE9X5(6*9);

        private final int size;

        BrowserSize(int size) {
            this.size = size;
        }

        public int value() {
            return size;
        }
    }

    public interface BrowserObjectConsumer<T> {
        void accept(Player player, T ability, ClickType click);
    }

    private final BrowserObjectConsumer<T> onClick;

    private final List<T> items;
    private final BiPredicate<Player, T> show;
    private final BiFunction<Player, T, GuiIcon> icon;
    private final BrowserSize size;
    private final int perPage;

    private final FloralGui parent;

    public ObjectBrowserGUI(BrowserSize size, FloralGui parent, Builder<T> builder) {
        super(size.size, Component.text("Items"));
        this.parent = parent;
        this.onClick = builder.onClick;
        this.show = builder.show;
        this.icon = builder.icon;
        this.items = builder.items;
        this.size = size;
        perPage = size.value() - 9;
    }

    public static GuiIcon simple(Component display) {
        return GuiIcon.material(Material.FEATHER).display(display);
    }

    public static class Builder<T> {
        private BrowserObjectConsumer<T> onClick = (p, i, c) -> {};
        private BiPredicate<Player, T> show = (p, i) -> true;
        private BiFunction<Player, T, GuiIcon> icon = (p, i) -> ObjectBrowserGUI.simple(Component.text("Item"));
        private List<T> items = new ArrayList<>();

        public Builder() {
        }

        public Builder<T> onClick(BrowserObjectConsumer<T> onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder<T> filter(BiPredicate<Player, T> show) {
            this.show = show;
            return this;
        }

        public Builder<T> icon(BiFunction<Player, T, GuiIcon> icon) {
            this.icon = icon;
            return this;
        }

        public Builder<T> items(List<T> items) {
            this.items = items;
            return this;
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

}
