package com.malaka.aat.internal.repository.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprDto;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprListDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.FacultySpr;

import java.util.List;
import java.util.Optional;

public interface FacultySprRepository extends JpaRepository<FacultySpr, String> {

    @Query("select new com.malaka.aat.internal.dto.spr.faculty.FacultySprDto(f.id, f.name, h) " +
            "from FacultySpr f left join User h on f.head.id = h.id")
    Page<FacultySprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.faculty.FacultySprDto(f.id, f.name, h) " +
            "from FacultySpr f left join User h on f.head.id = h.id where f.id = :id")
    Optional<FacultySprDto> findDtoById(String id);

    Optional<FacultySpr> findByHead(User head);

    Optional<FacultySpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.faculty.FacultySprListDto(f.id, f.name, " +
            "new com.malaka.aat.internal.dto.spr.faculty.FacultySprListDto$FacultyHeadDto(" +
            "h.id, concat(h.lastName, ' ', h.firstName, ' ', h.middleName), h.pinfl, h.phone)) " +
            "from FacultySpr f left join User h on f.head.id = h.id " +
            "order by f.name")
    List<FacultySprListDto> findAllForList();
}
