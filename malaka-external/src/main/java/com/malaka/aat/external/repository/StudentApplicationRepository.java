package com.malaka.aat.external.repository;

import com.malaka.aat.external.enumerators.student_application.StudentApplicationStatus;
import com.malaka.aat.external.model.StudentApplication;
import com.malaka.aat.external.model.spr.StudentApplicationStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, String> {

    @Query("SELECT COUNT(a) FROM StudentApplication a WHERE YEAR(a.instime) = YEAR(CURRENT_DATE)")
    Integer countAllApplicationsCreatedThisYear();

    @Query("from StudentApplication s WHERE (:userId is null or s.insuser = :userId) and (:status is null or s.status = :status)")
    Page<StudentApplication> findByInsuserAndStatus(@Param("userId")String userId , @Param("status") StudentApplicationStatus status, Pageable pageable);
}
