package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.dto.news.NewsCreateDto;
import com.malaka.aat.external.model.File;
import com.malaka.aat.external.model.News;
import com.malaka.aat.external.repository.NewsRepository;
import com.malaka.aat.external.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final FileService fileService;

    public ResponseWithPagination save(NewsCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        File file;
        try {
            file = fileService.save(dto.getImage());
            FileValidationUtil.validateImageFile(dto.getImage());
        } catch (IOException e) {
            throw new SystemException(e.getMessage());
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setImageFile(file);
        newsRepository.save(news);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

}
