package com.malaka.aat.internal.repository.spr;

import org.springframework.data.jpa.repository.JpaRepository;
import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.model.spr.StateHMet;

import java.util.Optional;

public interface StateHMetRepository extends JpaRepository<StateHMet, String> {


    Optional<StateHMet> findFirstByCourseOrderByHistSrn(Course course);


}
