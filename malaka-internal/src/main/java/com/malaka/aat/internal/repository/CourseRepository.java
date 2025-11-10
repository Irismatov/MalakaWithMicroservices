package com.malaka.aat.internal.repository;

import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Query("FROM Course c WHERE c.id in :ids")
    List<Course> findByIds(List<String> ids);


    @Query("FROM Course c WHERE (:name IS NULL OR c.name ILIKE CONCAT ('%', :name, '%')) AND " +
            "(:courseFormat IS NULL OR c.courseFormat.id = :courseFormat) AND " +
            "(:courseType IS NULL OR c.courseType.id = :courseType) AND " +
            "(:courseStudentType IS NULL OR c.courseStudentType.id = :courseStudentType) AND " +
            "(:state IS NULL or c.state LIKE :state)")
    Page<Course> getCoursesFilteredForAdmin(
            @Param("name") String name,
            @Param("courseFormat") Long courseFormat,
            @Param("courseType") Long courseType,
            @Param("courseStudentType") Long courseStudentType,
            @Param("state") String state,
            Pageable pageable
    );

        @Query("FROM Course c JOIN c.modules m " +
            "WHERE (:name IS NULL OR c.name ILIKE CONCAT ('%', :name, '%')) AND " +
            "(:courseFormat IS NULL OR c.courseFormat.id = :courseFormat) AND " +
            "(:courseType IS NULL OR c.courseType.id = :courseType) AND " +
            "(:courseStudentType IS NULL OR c.courseStudentType.id = :courseStudentType) AND " +
            "( (:state IS NULL and (c.state != '001') ) or c.state LIKE :state) AND " +
                "(m.teacher.id = :teacherId)")
    Page<Course> getCoursesFilteredForTeacher(
            @Param("teacherId") String teacherId,
            @Param("name") String name,
            @Param("courseFormat") Long courseFormat,
            @Param("courseType") Long courseType,
            @Param("courseStudentType") Long courseStudentType,
            @Param("state") String state,
            Pageable pageable
    );

    @Query("FROM Course c WHERE (:name IS NULL OR c.name ILIKE CONCAT ('%', :name, '%')) AND " +
            "(:courseFormat IS NULL OR c.courseFormat.id = :courseFormat) AND " +
            "(:courseType IS NULL OR c.courseType.id = :courseType) AND " +
            "(:courseStudentType IS NULL OR c.courseStudentType.id = :courseStudentType) AND " +
            "( (:state IS NULL AND (c.state = '003' OR c.state = '004' OR c.state = '005' OR c.state = '006' OR c.state = '007') ) OR c.state LIKE :state)")
    Page<Course> getCoursesFilteredForFacultyHead(
            @Param("name") String name,
            @Param("courseFormat") Long courseFormat,
            @Param("courseType") Long courseType,
            @Param("courseStudentType") Long courseStudentType,
            @Param("state") String state,
            Pageable pageable
    );


}
