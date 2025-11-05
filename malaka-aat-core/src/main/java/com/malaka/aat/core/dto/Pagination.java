package com.malaka.aat.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
    private int currentPage;
    private long numberOfElements;
    private long totalPages;
    private long totalElements;
}