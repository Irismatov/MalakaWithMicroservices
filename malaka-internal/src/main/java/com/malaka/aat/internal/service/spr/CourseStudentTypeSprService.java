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
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprCreateDto;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprDto;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprListDto;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprUpdateDto;
import com.malaka.aat.internal.model.spr.CourseStudentTypeSpr;
import com.malaka.aat.internal.repository.spr.CourseStudentTypeSprRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseStudentTypeSprService {

    private final CourseStudentTypeSprRepository courseStudentTypeSprRepository;

    public BaseResponse save(CourseStudentTypeSprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Course student type already exists with name: " + dto.getName());
        }

        CourseStudentTypeSpr courseStudentTypeSpr = new CourseStudentTypeSpr();
        courseStudentTypeSpr.setName(dto.getName());
        courseStudentTypeSpr.setDescription(dto.getDescription());

        // Generate next ID: find last ID and increment by 1
        Optional<CourseStudentTypeSpr> lastEntity = courseStudentTypeSprRepository.findLastByOrderByIdDesc();
        Long nextId = lastEntity.map(cst -> cst.getId() + 1).orElse(0L);
        courseStudentTypeSpr.setId(nextId);

        courseStudentTypeSprRepository.save(courseStudentTypeSpr);

        Page<CourseStudentTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<CourseStudentTypeSpr> byName = courseStudentTypeSprRepository.findByName(name);
        return byName.isPresent();
    }

    public Page<CourseStudentTypeSprDto> getLastUpdatedTypes() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return courseStudentTypeSprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<CourseStudentTypeSprDto> typePage = courseStudentTypeSprRepository.findAllDtos(pageRequest);
        response.setData(typePage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(Long id) {
        BaseResponse response = new BaseResponse();
        Optional<CourseStudentTypeSprDto> typeDto = courseStudentTypeSprRepository.findDtoById(id);
        if (typeDto.isEmpty()) {
            throw new BadRequestException("Course student type not found with id: " + id);
        }
        response.setData(typeDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(Long id, CourseStudentTypeSprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseStudentTypeSpr> optionalType = courseStudentTypeSprRepository.findById(id);
        if (optionalType.isEmpty()) {
            throw new BadRequestException("Course student type not found with id: " + id);
        }

        CourseStudentTypeSpr courseStudentTypeSpr = optionalType.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!courseStudentTypeSpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Course student type already exists with name: " + dto.getName());
            }
            courseStudentTypeSpr.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            courseStudentTypeSpr.setDescription(dto.getDescription());
        }

        courseStudentTypeSprRepository.save(courseStudentTypeSpr);
        Page<CourseStudentTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(Long id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<CourseStudentTypeSpr> optionalType = courseStudentTypeSprRepository.findById(id);
        if (optionalType.isEmpty()) {
            throw new BadRequestException("Course student type not found with id: " + id);
        }

        courseStudentTypeSprRepository.delete(optionalType.get());
        Page<CourseStudentTypeSprDto> lastUpdatedTypes = getLastUpdatedTypes();
        response.setData(lastUpdatedTypes, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<CourseStudentTypeSprListDto> types = courseStudentTypeSprRepository.findAllForList();
        response.setData(types);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public CourseStudentTypeSpr findById(Long id) {
        return courseStudentTypeSprRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Course student type not found with id: " + id));
    }
}
