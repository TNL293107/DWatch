package com.dwatch.common;

/**
 * Small page/size/totalPages helper shared by product listing and order
 * history so both compute pagination the same way.
 */
public final class Pagination {

    private final int currentPage;
    private final int pageSize;
    private final int totalItems;
    private final int totalPages;

    public Pagination(int requestedPage, int pageSize, int totalItems) {
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        this.currentPage = Math.max(1, requestedPage);
    }

    /** Parses a page query-param, defaulting to 1 for missing/invalid values. */
    public static int parsePage(String pageParam) {
        try {
            return Math.max(1, Integer.parseInt(pageParam));
        } catch (Exception e) {
            return 1;
        }
    }

    public int getCurrentPage() { return currentPage; }
    public int getPageSize()    { return pageSize; }
    public int getTotalItems()  { return totalItems; }
    public int getTotalPages()  { return totalPages; }
    public int getOffset()      { return (currentPage - 1) * pageSize; }
}
