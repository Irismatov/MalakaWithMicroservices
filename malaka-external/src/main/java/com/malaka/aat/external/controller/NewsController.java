package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.dto.news.NewsCreateDto;
import com.malaka.aat.external.dto.news.NewsUpdateDto;
import com.malaka.aat.external.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class NewsController {

    private final NewsService newsService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/news")
    public ResponseWithPagination save(
            @ModelAttribute @Validated NewsCreateDto dto
            ) {
        return newsService.save(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/news/{id}")
    public ResponseWithPagination update(@PathVariable String id, @ModelAttribute @Validated NewsUpdateDto dto) {
        return newsService.update(id, dto);
    };

}
