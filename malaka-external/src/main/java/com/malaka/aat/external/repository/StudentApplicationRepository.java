package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.StudentApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, String> {
}
