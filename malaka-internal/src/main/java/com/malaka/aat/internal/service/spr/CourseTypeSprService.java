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
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprCreateDto;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprDto;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprListDto;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprUpdateDto;
import com.malaka.aat.internal.model.spr.CourseTypeSpr;
import com.malaka.aat.internal.repository.spr.CourseTypeSprRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseTypeSprService {

    private final CourseTypeSprRepository courseTypeSprRepository;

    public BaseResponse save(CourseTypeSprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Course type already exists with name: " + dto.getName());
        }

        CourseTypeSpr courseTypeSpr = new CourseTypeSpr();
        courseTypeSpr.setName(dto.getName());
        courseTypeSpr.setDescription(dto.getDescription());

        // Generate next ID: find last ID and increment by 1
        Optional<CourseTypeSpr> lastEntity = courseTypeSprRepository.findLastByOrderByIdDesc();
        Long nextId = lastEntity.map(ct -> ct.getId() + 1).orElse(0L);
        courseTypeSpr.setId(nextId);

        courseTypeSprRepository.save(courseTypeSpr);

        Page<CourseTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<CourseTypeSpr> byName = courseTypeSprRepository.findByName(name);
        return byName.isPresent();
    }

    public Page<CourseTypeSprDto> getLastUpdatedTypes() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return courseTypeSprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<CourseTypeSprDto> typePage = courseTypeSprRepository.findAllDtos(pageRequest);
        response.setData(typePage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(Long id) {
        BaseResponse response = new BaseResponse();
        Optional<CourseTypeSprDto> typeDto = courseTypeSprRepository.findDtoById(id);
        if (typeDto.isEmpty()) {
            throw new BadRequestException("Course type not found with id: " + id);
        }
        response.setData(typeDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(Long id, CourseTypeSprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseTypeSpr> optionalType = courseTypeSprRepository.findById(id);
        if (optionalType.isEmpty()) {
            throw new BadRequestException("Course type not found with id: " + id);
        }

        CourseTypeSpr courseTypeSpr = optionalType.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!courseTypeSpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Course type already exists with name: " + dto.getName());
            }
            courseTypeSpr.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            courseTypeSpr.setDescription(dto.getDescription());
        }

        courseTypeSprRepository.save(courseTypeSpr);
        Page<CourseTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(Long id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseTypeSpr> optionalType = courseTypeSprRepository.findById(id);
        if (optionalType.isEmpty()) {
            throw new BadRequestException("Course type not found with id: " + id);
        }

        courseTypeSprRepository.delete(optionalType.get());
        Page<CourseTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<CourseTypeSprListDto> types = courseTypeSprRepository.findAllForList();
        response.setData(types);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public CourseTypeSpr findById(Long id) {
        return courseTypeSprRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Course type not found with id: " + id));
    }
}
