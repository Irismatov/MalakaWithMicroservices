package com.malaka.aat.external.repository.spr;

import com.malaka.aat.external.model.spr.LangSpr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LangSprRepository extends JpaRepository<LangSpr, Long> {
    Optional<LangSpr> findByName(String name);
}
