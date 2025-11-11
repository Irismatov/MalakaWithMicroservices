package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.dto.news.NewsCreateDto;
import com.malaka.aat.external.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/news")
    public ResponseWithPagination save(
            @ModelAttribute @Validated NewsCreateDto dto
            ) {
        return newsService.save(dto);
    }

}
