package com.malaka.aat.external.repository;

import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentDetailType;
import com.malaka.aat.external.model.StudentEnrollment;
import com.malaka.aat.external.model.StudentEnrollmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentEnrollmentDetailRepository extends JpaRepository<StudentEnrollmentDetail, String> {

    @Query(value = "from StudentEnrollmentDetail s where s.studentEnrollment = :studentEnrollment and s.isActive = 1")
    Optional<StudentEnrollmentDetail> findLastByStudentEnrollment(StudentEnrollment studentEnrollment);

    @Query(value = "from StudentEnrollmentDetail s where s.studentEnrollment = :studentEnrollment and s.type = :type order by s.instime desc limit 1")
    Optional<StudentEnrollmentDetail> findLastByStudentEnrollmentAndType(StudentEnrollment studentEnrollment, StudentEnrollmentDetailType type);

    @Query(value = "select s.moduleId from StudentEnrollmentDetail s " +
            "where s.studentEnrollment = :studentEnrollment and s.type = :type")
    List<String> findModuleIdsByStudentEnrollment(@Param("studentEnrollment") StudentEnrollment studentEnrollment,
                                                  @Param("type") StudentEnrollmentDetailType type);


    @Query(value = "select s.topicId from StudentEnrollmentDetail s " +
            "where s.studentEnrollment = :studentEnrollment and s.type = :type")
    List<String> findTopicIdsByStudentEnrollment(@Param("studentEnrollment") StudentEnrollment studentEnrollment,
                                                 @Param("type") StudentEnrollmentDetailType type);


    @Query(value = "select s.contentId from StudentEnrollmentDetail s " +
            "where s.studentEnrollment = :studentEnrollment and s.type = :type")
    List<String> findContentIdsByStudentEnrollmentAndType(@Param("studentEnrollment") StudentEnrollment studentEnrollment,
                                                          @Param("type") StudentEnrollmentDetailType type);
}
