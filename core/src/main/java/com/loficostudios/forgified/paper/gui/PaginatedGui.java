package com.loficostudios.forgified.paper.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public abstract class PaginatedGui extends AbstractFloralGui {
    private int page;

    public PaginatedGui(int size) {
        this(size, null);
    }

    public PaginatedGui(int size, Component title) {
        super(size,title);
    }

    @Override
    public void create(Player player) {
        clear();
        loadPage(player, page);
    }

    protected abstract void loadPage(Player player, int page);

    protected abstract int maxPage(Player player);

    public void nextPage(Player player) {
        if (page < maxPage(player) - 1) {
            page++;
            loadPage(player, page);
        }
    }

    public void previousPage(Player player) {
        if (page > 0) {
            page--;
            loadPage(player, page);
        }
    }

    /// Utility methods

    protected static int maxPage(List<?> items, int perPage) {
        return (int) Math.ceil((double) items.size() / perPage);
    }

    protected static <T> Collection<T> paginate(List<T> objects, int page, int itemsPerPage) {
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, objects.size());

        if (start >= objects.size()) {
            start = objects.size();
        }

        return objects.stream().toList().subList(start, end);
    }

}
