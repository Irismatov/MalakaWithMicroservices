package com.malaka.aat.external.repository;


import com.malaka.aat.external.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {

    @Query(value = "from Group g where g.courseId = :courseId order by g.instime desc limit 1")
    Optional<Group> findLastCreatedGroupByCourseId(String courseId);
}
