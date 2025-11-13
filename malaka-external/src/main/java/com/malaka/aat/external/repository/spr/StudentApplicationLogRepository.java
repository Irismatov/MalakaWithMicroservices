package com.malaka.aat.external.repository.spr;

import com.malaka.aat.external.model.StudentApplication;
import com.malaka.aat.external.model.spr.StudentApplicationStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentApplicationLogRepository extends JpaRepository<StudentApplicationStatusLog, String> {

    @Query("FROM StudentApplicationStatusLog l WHERE l.application = :application ORDER BY l.order DESC LIMIT 1")
    Optional<StudentApplicationStatusLog> findLastByApplication(StudentApplication application);
}
