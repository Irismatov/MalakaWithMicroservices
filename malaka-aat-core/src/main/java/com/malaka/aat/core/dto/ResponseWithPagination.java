package com.malaka.aat.core.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseWithPagination extends BaseResponse{
    private Pagination pagination;

    public void setData(Page<?> data, int currentPage) {
        List<?> content = data.getContent();
        super.setData(content);
        Pagination pagination = new Pagination();
        pagination.setTotalElements(data.getTotalElements());
        pagination.setTotalPages(data.getTotalPages());
        pagination.setCurrentPage(currentPage);
        pagination.setNumberOfElements(data.getNumberOfElements());
        this.pagination = pagination;
    }
}
