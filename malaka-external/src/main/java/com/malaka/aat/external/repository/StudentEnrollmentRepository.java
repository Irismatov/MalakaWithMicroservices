package com.malaka.aat.external.repository;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, String> {

    Optional<StudentEnrollment> findByStudentAndCourseIdAndGroup(Student student, String courseId, Group group);
}
