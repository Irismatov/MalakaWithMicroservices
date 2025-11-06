package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.StudentEnrollment;
import com.malaka.aat.external.model.StudentEnrollmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentEnrollmentDetailRepository extends JpaRepository<StudentEnrollmentDetail, String> {

    @Query(value = "from StudentEnrollmentDetail s where s.studentEnrollment = :studentEnrollment order by  s.instime desc limit 1")
    Optional<StudentEnrollmentDetail> findLastByStudentEnrollment(StudentEnrollment studentEnrollment);


}
