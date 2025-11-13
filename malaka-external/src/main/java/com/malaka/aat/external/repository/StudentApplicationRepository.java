package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.StudentApplication;
import com.malaka.aat.external.model.spr.StudentApplicationStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, String> {

    @Query("SELECT COUNT(a) FROM StudentApplication a WHERE YEAR(a.instime) = YEAR(CURRENT_DATE)")
    Integer countAllApplicationsCreatedThisYear();


    Page<StudentApplication> findAllByInsuser(String id, Pageable pageable);
}
