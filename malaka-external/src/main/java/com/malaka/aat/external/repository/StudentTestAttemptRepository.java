package com.malaka.aat.external.repository;


import com.malaka.aat.external.enumerators.TestAttemptType;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, String> {
    List<StudentTestAttempt> findByGroupAndTopicId(Group group, String topicId);

    List<StudentTestAttempt> findByStudentAndGroupAndTestId(Student student, Group group, String testId);


    List<StudentTestAttempt> findByGroupAndStudentAndModuleIdAndTopicIdAndTestIdAndType (
            Group group, Student student, String moduleId, String topicId, String testId, TestAttemptType type
    );
}
