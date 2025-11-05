package com.malaka.aat.external.repository.spr;

import com.malaka.aat.external.model.spr.StudentTypeSpr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StudentTypeSprRepository extends JpaRepository<StudentTypeSpr, Long> {


    Optional<StudentTypeSpr> findByName(String name);


}
