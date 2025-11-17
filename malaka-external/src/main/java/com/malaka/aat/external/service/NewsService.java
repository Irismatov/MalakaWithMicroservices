package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.Pagination;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.core.util.ServiceUtil;
import com.malaka.aat.external.dto.news.NewsCreateDto;
import com.malaka.aat.external.dto.news.NewsListItemDto;
import com.malaka.aat.external.dto.news.NewsUpdateDto;
import com.malaka.aat.external.model.File;
import com.malaka.aat.external.model.News;
import com.malaka.aat.external.repository.NewsRepository;
import com.malaka.aat.external.util.FileValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${app.projectUrl}")
    private String projectUrl;

    private final NewsRepository newsRepository;
    private final FileService fileService;

    @Transactional
    public ResponseWithPagination save(NewsCreateDto dto) {

        // file
        File file;
        try {
            file = fileService.save(dto.getImage());
            FileValidationUtil.validateImageFile(dto.getImage());
        } catch (IOException e) {
            throw new SystemException(e.getMessage());
        }

        // set fields and save
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setImageFile(file);
        newsRepository.save(news);


        // response
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<News> pageList = newsRepository.findAll(pageRequest);
        List<NewsListItemDto> list = pageList.getContent().stream().map(this::convertEntityToListItemDto).toList();
        Pagination pagination = new Pagination();
        pagination.setCurrentPage(0);
        pagination.setTotalElements(pageList.getTotalElements());
        pagination.setTotalPages(pageList.getTotalPages());
        pagination.setNumberOfElements(pageList.getNumberOfElements());
        response.setPagination(pagination);
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(String id, NewsUpdateDto dto) {
        News news = newsRepository.findById(id).orElseThrow(
                () -> new NotFoundException("News not found with id: " + id)
        );
        if (dto.getTitle() != null) {

        }


        ResponseWithPagination response = new ResponseWithPagination();
        return response;
    }

    @Transactional
    public ResponseWithPagination delete(String id) {
        ResponseWithPagination response = new ResponseWithPagination();
        newsRepository.deleteById(id);


        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination getAll() {
        return null;
    }

    public BaseResponse getDetail(String id) {
        return null;
    }

    public NewsListItemDto convertEntityToListItemDto(News entity) {
        NewsListItemDto dto = new NewsListItemDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setText(entity.getText());
        dto.setInstime(entity.getInstime());
        String publicUrl = ServiceUtil.convertToPublicUrl(entity.getImageFile().getPath(), projectUrl);
        dto.setImageUrl(publicUrl);
        return dto;
    }

}
