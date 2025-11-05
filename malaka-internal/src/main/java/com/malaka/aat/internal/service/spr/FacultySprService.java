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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprCreateDto;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprDto;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprListDto;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprUpdateDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.FacultySpr;
import com.malaka.aat.internal.repository.spr.FacultySprRepository;
import com.malaka.aat.internal.service.UserService;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FacultySprService {

    private final FacultySprRepository facultySprRepository;
    private final UserService userService;

    public BaseResponse save(FacultySprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        FacultySpr facultySpr = new FacultySpr();
        facultySpr.setName(dto.getName());

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Faculty spr already exists with name: " + dto.getName());
        }

        if (dto.getHeadId() != null) {
            setFacultyHead(facultySpr, dto);
        }

        facultySprRepository.save(facultySpr);
        Page<FacultySprDto> lastUpdatedFaculties = getLastUpdatedFaculties();
        response.setData(lastUpdatedFaculties, 0);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<FacultySpr> byName = facultySprRepository.findByName(name);
        return  byName.isPresent();
    }

    private void setFacultyHead(FacultySpr facultySpr, FacultySprCreateDto dto) {
        try {
            User facultyHead = userService.getFacultyHead(dto.getHeadId());
            Optional<FacultySpr> byHead = facultySprRepository.findByHead(facultyHead);
            if (byHead.isPresent()) {
                throw new AlreadyExistsException("Faculty head is already assigned: " +  dto.getHeadId());
            }
            facultySpr.setHead(facultyHead);
        } catch (AccessDeniedException e) {
            throw new BadRequestException("Error finding faculty head with id " + dto.getHeadId());
        }
    }

    public Page<FacultySprDto> getLastUpdatedFaculties() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return facultySprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<FacultySprDto> facultyPage = facultySprRepository.findAllDtos(pageRequest);
        response.setData(facultyPage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(String id) {
        BaseResponse response = new BaseResponse();
        Optional<FacultySprDto> facultyDto = facultySprRepository.findDtoById(id);
        if (facultyDto.isEmpty()) {
            throw new BadRequestException("Faculty not found with id: " + id);
        }
        response.setData(facultyDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(String id, FacultySprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<FacultySpr> optionalFaculty = facultySprRepository.findById(id);
        if (optionalFaculty.isEmpty()) {
            throw new BadRequestException("Faculty not found with id: " + id);
        }

        FacultySpr facultySpr = optionalFaculty.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!facultySpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Faculty spr already exists with name: " + dto.getName());
            }
            facultySpr.setName(dto.getName());
        }

        if (dto.getHeadId() != null && !dto.getHeadId().isBlank()) {
            User newHead = userService.getFacultyHead(dto.getHeadId());
            if (facultySpr.getHead() == null || !facultySpr.getHead().getId().equals(dto.getHeadId())) {
                Optional<FacultySpr> byHead = facultySprRepository.findByHead(newHead);
                if (byHead.isPresent() && !byHead.get().getId().equals(id)) {
                    throw new AlreadyExistsException("Faculty head is already assigned: " + dto.getHeadId());
                }
                facultySpr.setHead(newHead);
            }
        }

        facultySprRepository.save(facultySpr);
        Page<FacultySprDto> lastUpdatedFaculties = getLastUpdatedFaculties();
        response.setData(lastUpdatedFaculties, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(String id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<FacultySpr> optionalFaculty = facultySprRepository.findById(id);
        if (optionalFaculty.isEmpty()) {
            throw new BadRequestException("Faculty not found with id: " + id);
        }

        facultySprRepository.delete(optionalFaculty.get());
        Page<FacultySprDto> lastUpdatedFaculties = getLastUpdatedFaculties();
        response.setData(lastUpdatedFaculties, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<FacultySprListDto> faculties = facultySprRepository.findAllForList();
        response.setData(faculties);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

}
