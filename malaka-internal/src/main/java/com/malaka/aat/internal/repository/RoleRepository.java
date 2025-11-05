package com.malaka.aat.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.malaka.aat.internal.dto.spr.role.RoleDto;
import com.malaka.aat.internal.dto.spr.role.RoleListDto;
import com.malaka.aat.internal.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.role.RoleDto(r.id, r.name, r.description) " +
            "from role r")
    Page<RoleDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.role.RoleDto(r.id, r.name, r.description) " +
            "from role r where r.id = :id")
    Optional<RoleDto> findDtoById(String id);

    @Query("select new com.malaka.aat.internal.dto.spr.role.RoleListDto(r.id, r.name, r.description) " +
            "from role r order by r.name")
    List<RoleListDto> findAllForList();

}
