package com.malaka.aat.external.repository;


import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, String> {
    List<StudentTestAttempt> findByGroupAndTopicId(Group group, String topicId);
}
