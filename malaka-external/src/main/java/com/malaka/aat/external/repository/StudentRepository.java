package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.User;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByType(StudentTypeSpr type);

    Optional<Student> findByUser(User user);
}
