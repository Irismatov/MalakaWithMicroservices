package com.malaka.aat.internal.repository.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.malaka.aat.internal.dto.spr.lang.LangSprDto;
import com.malaka.aat.internal.dto.spr.lang.LangSprListDto;
import com.malaka.aat.internal.model.spr.LangSpr;

import java.util.List;
import java.util.Optional;

public interface LangSprRepository extends JpaRepository<LangSpr, Long> {

    @Query("select new com.malaka.aat.internal.dto.spr.lang.LangSprDto(l.id, l.name) " +
            "from LangSpr l")
    Page<LangSprDto> findAllDtos(Pageable pageRequest);

    @Query("select new com.malaka.aat.internal.dto.spr.lang.LangSprDto(l.id, l.name) " +
            "from LangSpr l where l.id = :id")
    Optional<LangSprDto> findDtoById(Long id);

    Optional<LangSpr> findByName(String name);

    @Query("select new com.malaka.aat.internal.dto.spr.lang.LangSprListDto(l.id, l.name) " +
            "from LangSpr l " +
            "order by l.name")
    List<LangSprListDto> findAllForList();

    @Query("select l from LangSpr l order by l.id desc limit 1")
    Optional<LangSpr> findLastByOrderByIdDesc();
}
