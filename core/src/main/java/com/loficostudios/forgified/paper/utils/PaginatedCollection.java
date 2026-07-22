package com.loficostudios.forgified.paper.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaginatedCollection<T> {
    private final List<T> items;
    private final int perPage;

    public PaginatedCollection(List<T> items, int perPage) {
        this.items = items;
        this.perPage = perPage;
    }

    public PaginatedCollection(T[] items, int perPage) {
        this.items =  Arrays.stream(items).toList();
        this.perPage = perPage;
    }

    public int getMaxPage() {
        return (items.size() + perPage - 1) / perPage;
    }

    public int getPageOf(T item) {
        int index = item == null ? 0 : items.indexOf(item);
        return (index / perPage) + 1;
    }

    public List<T> getPage(int page) {
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, items.size());
        if (start >= items.size()) return Collections.emptyList();
        return items.subList(start, end);
    }

    public int getCurrentIndex(T item) {
        return item == null ? 0 : items.indexOf(item);
    }

    public List<T> getItems() {
        return items;
    }
}
