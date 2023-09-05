package com.example.princesstown.service.comment;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationInfo {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;

    public PaginationInfo(int currentPage, int pageSize, int totalPages, long totalItems) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }
}
