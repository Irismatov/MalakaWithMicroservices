package com.malaka.aat.internal.repository.spr;

import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprDto;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprListDto;
import com.malaka.aat.internal.model.spr.CourseFormatSpr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseFormatSprRepository extends JpaRepository<CourseFormatSpr, Long> {

    @Query("select new com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprDto(cf.id, cf.name, cf.description) " +
            "from CourseFormatSpr cf")
    Page<CourseFormatSprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprDto(cf.id, cf.name, cf.description) " +
            "from CourseFormatSpr cf where cf.id = :id")
    Optional<CourseFormatSprDto> findDtoById(Long id);

    Optional<CourseFormatSpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprListDto(cf.id, cf.name) " +
            "from CourseFormatSpr cf " +
            "order by cf.name")
    List<CourseFormatSprListDto> findAllForList();

    @Query("select cf from CourseFormatSpr cf order by cf.id desc limit 1")
    Optional<CourseFormatSpr> findLastByOrderByIdDesc();
}
