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
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprCreateDto;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprDto;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprListDto;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprUpdateDto;
import com.malaka.aat.internal.model.spr.CourseFormatSpr;
import com.malaka.aat.internal.repository.spr.CourseFormatSprRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseFormatSprService {

    private final CourseFormatSprRepository courseFormatSprRepository;

    public BaseResponse save(CourseFormatSprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Course format already exists with name: " + dto.getName());
        }

        CourseFormatSpr courseFormatSpr = new CourseFormatSpr();
        courseFormatSpr.setName(dto.getName());
        courseFormatSpr.setDescription(dto.getDescription());

        // Generate next ID: find last ID and increment by 1
        Optional<CourseFormatSpr> lastEntity = courseFormatSprRepository.findLastByOrderByIdDesc();
        Long nextId = lastEntity.map(cf -> cf.getId() + 1).orElse(0L);
        courseFormatSpr.setId(nextId);

        courseFormatSprRepository.save(courseFormatSpr);

        Page<CourseFormatSprDto> lastUpdatedFormats = getLastUpdatedFormats();
        response.setData(lastUpdatedFormats, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<CourseFormatSpr> byName = courseFormatSprRepository.findByName(name);
        return byName.isPresent();
    }

    public Page<CourseFormatSprDto> getLastUpdatedFormats() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return courseFormatSprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<CourseFormatSprDto> formatPage = courseFormatSprRepository.findAllDtos(pageRequest);
        response.setData(formatPage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(Long id) {
        BaseResponse response = new BaseResponse();
        Optional<CourseFormatSprDto> formatDto = courseFormatSprRepository.findDtoById(id);
        if (formatDto.isEmpty()) {
            throw new BadRequestException("Course format not found with id: " + id);
        }
        response.setData(formatDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(Long id, CourseFormatSprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseFormatSpr> optionalFormat = courseFormatSprRepository.findById(id);
        if (optionalFormat.isEmpty()) {
            throw new BadRequestException("Course format not found with id: " + id);
        }

        CourseFormatSpr courseFormatSpr = optionalFormat.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!courseFormatSpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Course format already exists with name: " + dto.getName());
            }
            courseFormatSpr.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            courseFormatSpr.setDescription(dto.getDescription());
        }

        courseFormatSprRepository.save(courseFormatSpr);
        Page<CourseFormatSprDto> lastUpdatedFormats = getLastUpdatedFormats();
        response.setData(lastUpdatedFormats, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(Long id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseFormatSpr> optionalFormat = courseFormatSprRepository.findById(id);
        if (optionalFormat.isEmpty()) {
            throw new BadRequestException("Course format not found with id: " + id);
        }

        courseFormatSprRepository.delete(optionalFormat.get());
        Page<CourseFormatSprDto> lastUpdatedFormats = getLastUpdatedFormats();
        response.setData(lastUpdatedFormats, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<CourseFormatSprListDto> formats = courseFormatSprRepository.findAllForList();
        response.setData(formats);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public CourseFormatSpr findById(Long id) {
        return courseFormatSprRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Course format not found with id: " + id));
    }
}
