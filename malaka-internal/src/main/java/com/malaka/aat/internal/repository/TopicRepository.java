package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.malaka.aat.internal.model.Topic;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

    List<Topic> findByModuleIdOrderByOrderAsc(String moduleId);

    Optional<Topic> findByModuleIdAndOrder(String moduleId, Integer order);

    boolean existsByModuleIdAndName(String moduleId, String name);

    /**
     * Find topic by ID with eagerly fetched Test and its questions.
     * This prevents LazyInitializationException when accessing test in DTOs.
     */
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.test test LEFT JOIN FETCH test.questions WHERE t.id = :id AND t.isDeleted = 0")
    Optional<Topic> findByIdWithTest(@Param("id") String id);
}
