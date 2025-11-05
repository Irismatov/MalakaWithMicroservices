package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.malaka.aat.internal.model.Module;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, String> {
    @Query("SELECT m FROM Module m WHERE m.name = :name AND m.course.id = :courseId")
    Optional<Module> findByNameAndCourseId(@Param("name") String name, @Param("courseId") String courseId);

    @Query("SELECT COUNT(m) FROM Module m WHERE m.course.id = :courseId")
    Long countByCourseId(@Param("courseId") String courseId);

    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId AND m.order = :order")
    Optional<Module> findByCourseIdAndOrder(@Param("courseId") String courseId, @Param("order") Integer order);

    Optional<Module> findByTeacherId(String teacherId);
}
