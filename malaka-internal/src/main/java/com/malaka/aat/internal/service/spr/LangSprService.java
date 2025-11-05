package com.malaka.aat.internal.service.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.AlreadyExistsException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.dto.spr.lang.LangSprCreateDto;
import com.malaka.aat.internal.dto.spr.lang.LangSprDto;
import com.malaka.aat.internal.dto.spr.lang.LangSprListDto;
import com.malaka.aat.internal.dto.spr.lang.LangSprUpdateDto;
import com.malaka.aat.internal.model.spr.LangSpr;
import com.malaka.aat.internal.repository.spr.LangSprRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LangSprService {

    private final LangSprRepository langSprRepository;

    public BaseResponse save(LangSprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Language already exists with name: " + dto.getName());
        }

        LangSpr langSpr = new LangSpr();
        langSpr.setName(dto.getName());

        // Generate next ID: find last ID and increment by 1, starting from 0
        Optional<LangSpr> lastEntity = langSprRepository.findLastByOrderByIdDesc();
        Long nextId = lastEntity.map(l -> l.getId() + 1).orElse(0L);
        langSpr.setId(nextId);

        langSprRepository.save(langSpr);

        Page<LangSprDto> lastUpdatedLangs = getLastUpdatedLangs();
        response.setData(lastUpdatedLangs, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<LangSpr> byName = langSprRepository.findByName(name);
        return byName.isPresent();
    }

    public Page<LangSprDto> getLastUpdatedLangs() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return langSprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<LangSprDto> langPage = langSprRepository.findAllDtos(pageRequest);
        response.setData(langPage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(Long id) {
        BaseResponse response = new BaseResponse();
        Optional<LangSprDto> langDto = langSprRepository.findDtoById(id);
        if (langDto.isEmpty()) {
            throw new BadRequestException("Language not found with id: " + id);
        }
        response.setData(langDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(Long id, LangSprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<LangSpr> optionalLang = langSprRepository.findById(id);
        if (optionalLang.isEmpty()) {
            throw new BadRequestException("Language not found with id: " + id);
        }

        LangSpr langSpr = optionalLang.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!langSpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Language already exists with name: " + dto.getName());
            }
            langSpr.setName(dto.getName());
        }

        langSprRepository.save(langSpr);
        Page<LangSprDto> lastUpdatedLangs = getLastUpdatedLangs();
        response.setData(lastUpdatedLangs, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(Long id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<LangSpr> optionalLang = langSprRepository.findById(id);
        if (optionalLang.isEmpty()) {
            throw new BadRequestException("Language not found with id: " + id);
        }

        langSprRepository.delete(optionalLang.get());
        Page<LangSprDto> lastUpdatedLangs = getLastUpdatedLangs();
        response.setData(lastUpdatedLangs, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<LangSprListDto> langs = langSprRepository.findAllForList();
        response.setData(langs);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public LangSpr findById(Long id) {
        return langSprRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Language not found with id: " + id));
    }
}
