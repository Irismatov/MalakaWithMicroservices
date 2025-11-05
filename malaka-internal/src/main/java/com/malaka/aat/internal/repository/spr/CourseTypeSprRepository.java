package com.malaka.aat.internal.repository.spr;

import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprDto;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprListDto;
import com.malaka.aat.internal.model.spr.CourseTypeSpr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface CourseTypeSprRepository extends JpaRepository<CourseTypeSpr, Long> {

    @Query("select new com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprDto(ct.id, ct.name, ct.description) " +
            "from CourseTypeSpr ct")
    Page<CourseTypeSprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprDto(ct.id, ct.name, ct.description) " +
            "from CourseTypeSpr ct where ct.id = :id")
    Optional<CourseTypeSprDto> findDtoById(Long id);

    Optional<CourseTypeSpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprListDto(ct.id, ct.name) " +
            "from CourseTypeSpr ct " +
            "order by ct.name")
    List<CourseTypeSprListDto> findAllForList();

    @Query("select ct from CourseTypeSpr ct order by ct.id desc limit 1")
    Optional<CourseTypeSpr> findLastByOrderByIdDesc();
}
