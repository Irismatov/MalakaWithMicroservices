package com.malaka.aat.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.malaka.aat.internal.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByName(String name);

    // Find courses created by specific user (Methodist)
    @Query("SELECT c FROM Course c WHERE c.insuser = :userId")
    List<Course> findByInsuser(@Param("userId") String userId);

    // Find courses where user is assigned as teacher in any module
    @Query("SELECT DISTINCT c FROM Course c JOIN c.modules m WHERE m.teacher.id = :teacherId")
    List<Course> findCoursesByTeacherId(@Param("teacherId") String teacherId);
}
