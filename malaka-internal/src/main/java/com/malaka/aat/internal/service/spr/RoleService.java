package com.malaka.aat.internal.service.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.AlreadyExistsException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.dto.spr.role.RoleCreateDto;
import com.malaka.aat.internal.dto.spr.role.RoleDto;
import com.malaka.aat.internal.dto.spr.role.RoleListDto;
import com.malaka.aat.internal.dto.spr.role.RoleUpdateDto;
import com.malaka.aat.internal.model.Role;
import com.malaka.aat.internal.repository.RoleRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public BaseResponse save(RoleCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isNameExists(dto.getName())) {
            throw new AlreadyExistsException("Role already exists with name: " + dto.getName());
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        roleRepository.save(role);
        Page<RoleDto> lastUpdatedRoles = getLastUpdatedRoles();
        response.setData(lastUpdatedRoles, 0);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isNameExists(String name) {
        Optional<Role> byName = roleRepository.findByName(name);
        return byName.isPresent();
    }

    public Page<RoleDto> getLastUpdatedRoles() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        return roleRepository.findAllDtos(pageRequest);
    }

    public ResponseWithPagination getList(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);
        Page<RoleDto> rolePage = roleRepository.findAllDtos(pageRequest);
        response.setData(rolePage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getById(String id) {
        BaseResponse response = new BaseResponse();
        Optional<RoleDto> roleDto = roleRepository.findDtoById(id);
        if (roleDto.isEmpty()) {
            throw new BadRequestException("Role not found with id: " + id);
        }
        response.setData(roleDto.get());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(String id, RoleUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isEmpty()) {
            throw new BadRequestException("Role not found with id: " + id);
        }

        Role role = optionalRole.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            if (!role.getName().equals(dto.getName()) && isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Role already exists with name: " + dto.getName());
            }
            role.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }

        roleRepository.save(role);
        Page<RoleDto> lastUpdatedRoles = getLastUpdatedRoles();
        response.setData(lastUpdatedRoles, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(String id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isEmpty()) {
            throw new BadRequestException("Role not found with id: " + id);
        }

        roleRepository.delete(optionalRole.get());
        Page<RoleDto> lastUpdatedRoles = getLastUpdatedRoles();
        response.setData(lastUpdatedRoles, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getAllForList() {
        BaseResponse response = new BaseResponse();
        List<RoleListDto> roles = roleRepository.findAllForList();
        response.setData(roles);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public Role findByName(String name) {
        Optional<Role> byName = roleRepository.findByName(name);
        return byName.orElseThrow(() -> new NotFoundException("Role not found with name: " + name));
    }

    public Set<Role> findRolesByNames(String... names) {
        Set<Role> roles = new HashSet<>();
        for (String name : names) {
            Role role = findByName(name);
            roles.add(role);
        }
        return roles;
    }

    public Role findById(String id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
    }
}
