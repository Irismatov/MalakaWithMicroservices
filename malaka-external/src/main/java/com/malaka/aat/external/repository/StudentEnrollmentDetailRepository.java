package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.StudentEnrollment;
import com.malaka.aat.external.model.StudentEnrollmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentEnrollmentDetailRepository extends JpaRepository<StudentEnrollmentDetail, String> {

    @Query(value = "from StudentEnrollmentDetail s where s.studentEnrollment = :studentEnrollment and s.isActive = 1")
    Optional<StudentEnrollmentDetail> findLastByStudentEnrollment(StudentEnrollment studentEnrollment);


}
