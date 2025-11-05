package com.malaka.aat.internal.repository.spr;

import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprDto;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprListDto;
import com.malaka.aat.internal.model.spr.CourseStudentTypeSpr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface CourseStudentTypeSprRepository extends JpaRepository<CourseStudentTypeSpr, Long> {

    @Query("select new com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprDto(cst.id, cst.name, cst.description) " +
            "from CourseStudentTypeSpr cst")
    Page<CourseStudentTypeSprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprDto(cst.id, cst.name, cst.description) " +
            "from CourseStudentTypeSpr cst where cst.id = :id")
    Optional<CourseStudentTypeSprDto> findDtoById(Long id);

    Optional<CourseStudentTypeSpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprListDto(cst.id, cst.name) " +
            "from CourseStudentTypeSpr cst " +
            "order by cst.name")
    List<CourseStudentTypeSprListDto> findAllForList();

    @Query("select cst from CourseStudentTypeSpr cst order by cst.id desc limit 1")
    Optional<CourseStudentTypeSpr> findLastByOrderByIdDesc();
}
