package com.malaka.aat.internal.repository.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprDto;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprListDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.DepartmentSpr;

import java.util.List;
import java.util.Optional;

public interface DepartmentSprRepository extends JpaRepository<DepartmentSpr, String> {

    @Query("select new com.malaka.aat.internal.dto.spr.department.DepartmentSprDto(d.ID, d.name, h, f) " +
            "from DepartmentSpr d " +
            "left join User h on d.user.id = h.id " +
            "left join FacultySpr f on d.facultySpr.id = f.id")
    Page<DepartmentSprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.department.DepartmentSprDto(d.ID, d.name, h, f) " +
            "from DepartmentSpr d " +
            "left join User h on d.user.id = h.id " +
            "left join FacultySpr f on d.facultySpr.id = f.id " +
            "where (:facultyId is null or f.id = :facultyId)")
    Page<DepartmentSprDto> findAllDtosWithFilter(@Param("facultyId") String facultyId, Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.department.DepartmentSprDto(d.ID, d.name, h, f) " +
            "from DepartmentSpr d " +
            "left join User h on d.user.id = h.id " +
            "left join FacultySpr f on d.facultySpr.id = f.id " +
            "where d.ID = :id")
    Optional<DepartmentSprDto> findDtoById(String id);

    Optional<DepartmentSpr> findByUser(User user);

    Optional<DepartmentSpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.department.DepartmentSprListDto(d.ID, d.name, " +
            "new com.malaka.aat.internal.dto.spr.department.DepartmentSprListDto$DepartmentHeadDto(" +
            "h.id, concat(h.lastName, ' ', h.firstName, ' ', h.middleName), h.pinfl, h.phone), " +
            "new com.malaka.aat.internal.dto.spr.department.DepartmentSprListDto$FacultyInfoDto(f.id, f.name)) " +
            "from DepartmentSpr d " +
            "left join User h on d.user.id = h.id " +
            "left join FacultySpr f on d.facultySpr.id = f.id " +
            "where (:facultyId is null or f.id = :facultyId) " +
            "order by d.name")
    List<DepartmentSprListDto> findAllForList(@Param("facultyId") String facultyId);
}
