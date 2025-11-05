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
import com.malaka.aat.internal.dto.spr.department.DepartmentSprCreateDto;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprDto;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprListDto;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprUpdateDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.DepartmentSpr;
import com.malaka.aat.internal.model.spr.FacultySpr;
import com.malaka.aat.internal.repository.spr.DepartmentSprRepository;
import com.malaka.aat.internal.repository.spr.FacultySprRepository;
import com.malaka.aat.internal.service.UserService;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DepartmentSprService {

    private final DepartmentSprRepository departmentSprRepository;
    private final FacultySprRepository facultySprRepository;
    private final UserService userService;

    public BaseResponse save(DepartmentSprCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        DepartmentSpr departmentSpr = new DepartmentSpr();
        departmentSpr.setName(dto.getName());

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Department spr already exists with name: " + dto.getName());
        }

        // Set faculty
        Optional<FacultySpr> optionalFaculty = facultySprRepository.findById(dto.getFacultyId());
        if (optionalFaculty.isEmpty()) {
            throw new BadRequestException("Faculty not found with id: " + dto.getFacultyId());
        }
        departmentSpr.setFacultySpr(optionalFaculty.get());

        if (dto.getHeadId() != null && !dto.getHeadId().isBlank()) {
            setDepartmentHead(departmentSpr, dto);
        }

        departmentSprRepository.save(departmentSpr);
        Page<DepartmentSprDto> lastUpdatedDepartments = getLastUpdatedDepartments();
        response.setData(lastUpdatedDepartments, 0);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<DepartmentSpr> byName = departmentSprRepository.findByName(name);
        return byName.isPresent();
    }

    private void setDepartmentHead(DepartmentSpr departmentSpr, DepartmentSprCreateDto dto) {
        try {
            User departmentHead = userService.getFacultyHead(dto.getHeadId());
            Optional<DepartmentSpr> byHead = departmentSprRepository.findByUser(departmentHead);
            if (byHead.isPresent()) {
                throw new AlreadyExistsException("Department head is already assigned: " + dto.getHeadId());
            }
            departmentSpr.setUser(departmentHead);
        } catch (AccessDeniedException e) {
            throw new BadRequestException("Error finding department head with id " + dto.getHeadId());
        }
    }

    public Page<DepartmentSprDto> getLastUpdatedDepartments() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return departmentSprRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size, String facultyId) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<DepartmentSprDto> departmentPage = departmentSprRepository.findAllDtosWithFilter(facultyId, pageRequest);
        response.setData(departmentPage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(String id) {
        BaseResponse response = new BaseResponse();
        Optional<DepartmentSprDto> departmentDto = departmentSprRepository.findDtoById(id);
        if (departmentDto.isEmpty()) {
            throw new BadRequestException("Department not found with id: " + id);
        }
        response.setData(departmentDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(String id, DepartmentSprUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<DepartmentSpr> optionalDepartment = departmentSprRepository.findById(id);
        if (optionalDepartment.isEmpty()) {
            throw new BadRequestException("Department not found with id: " + id);
        }

        DepartmentSpr departmentSpr = optionalDepartment.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!departmentSpr.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Department spr already exists with name: " + dto.getName());
            }
            departmentSpr.setName(dto.getName());
        }

        if (dto.getFacultyId() != null && !dto.getFacultyId().isBlank()) {
            Optional<FacultySpr> optionalFaculty = facultySprRepository.findById(dto.getFacultyId());
            if (optionalFaculty.isEmpty()) {
                throw new BadRequestException("Faculty not found with id: " + dto.getFacultyId());
            }
            departmentSpr.setFacultySpr(optionalFaculty.get());
        }

        if (dto.getHeadId() != null && !dto.getHeadId().isBlank()) {
            User newHead = userService.getFacultyHead(dto.getHeadId());
            if (departmentSpr.getUser() == null || !departmentSpr.getUser().getId().equals(dto.getHeadId())) {
                Optional<DepartmentSpr> byHead = departmentSprRepository.findByUser(newHead);
                if (byHead.isPresent() && !byHead.get().getID().equals(id)) {
                    throw new AlreadyExistsException("Department head is already assigned: " + dto.getHeadId());
                }
                departmentSpr.setUser(newHead);
            }
        }

        departmentSprRepository.save(departmentSpr);
        Page<DepartmentSprDto> lastUpdatedDepartments = getLastUpdatedDepartments();
        response.setData(lastUpdatedDepartments, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(String id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<DepartmentSpr> optionalDepartment = departmentSprRepository.findById(id);
        if (optionalDepartment.isEmpty()) {
            throw new BadRequestException("Department not found with id: " + id);
        }

        departmentSprRepository.delete(optionalDepartment.get());
        Page<DepartmentSprDto> lastUpdatedDepartments = getLastUpdatedDepartments();
        response.setData(lastUpdatedDepartments, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList(String facultyId) {
        BaseResponse response = new BaseResponse();
        List<DepartmentSprListDto> departments = departmentSprRepository.findAllForList(facultyId);
        response.setData(departments);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
