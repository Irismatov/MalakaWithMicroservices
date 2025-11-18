package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.User;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByType(StudentTypeSpr type);

    Optional<Student> findByUser(User user);

    @Query(value = "from Student s where s.type.id = :typeId and " +
            "(:search is null or s.user.firstName ilike '%' || :search || '%' or s.user.lastName ilike '%' || :search || '%')")
    Page<Student> findAllByTypeAndSearch(Integer typeId, String search, Pageable pageable);
}
