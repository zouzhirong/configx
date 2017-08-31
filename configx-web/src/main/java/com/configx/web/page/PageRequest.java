package com.configx.web.page;

public class PageRequest {
    private final int page;

    private final int size;

    /**
     * Creates a new {@link PageRequest}. Pages are one indexed, thus providing 1 for {@code page} will return
     * the first page.
     * 
     * @param page must not be less than one.
     * @param size must not be less than one.
     */
    public PageRequest(int page, int size) {

        if (page < 1) {
            throw new IllegalArgumentException("Page index must not be less than one!");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}