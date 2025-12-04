package com.malaka.aat.external.repository;


import com.malaka.aat.external.enumerators.TestAttemptType;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, String> {
    List<StudentTestAttempt> findByGroupAndTopicId(Group group, String topicId);

    List<StudentTestAttempt> findByStudentAndGroupAndTestId(Student student, Group group, String testId);


    List<StudentTestAttempt> findByGroupAndStudentAndModuleIdAndTopicIdAndTestIdAndType (
            Group group, Student student, String moduleId, String topicId, String testId, TestAttemptType type
    );

    @Query(value = """
        from StudentTestAttempt s where s.group = :group and s.moduleId = :moduleId and s.topicId = :topicId
        and s.testId = :testId and s.type = :testAttemptType order by s.attemptNumber asc
""")
    List<StudentTestAttempt> findByGroupAndModuleIdAndTopicIdAndTestIdAndTypeOrderedByOrder(
            @Param("group") Group group,
            @Param("moduleId") String moduleId,
            @Param("topicId") String topicId,
            @Param("testId") String testId,
            @Param("testAttemptType") TestAttemptType testAttemptType);
}
