package com.malaka.aat.external.model;


import com.malaka.aat.external.enumerators.TestAttemptState;
import com.malaka.aat.external.enumerators.TestAttemptType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table(name = "student_test_attempt")
@Entity
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_test_attempt SET is_deleted = 1 WHERE id = ?")
public class StudentTestAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 50)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    @Column(name = "module_id", nullable = false, length = 50)
    private String moduleId;
    @Column(name = "topic_id", nullable = false, length = 50)
    private String topicId;
    @Column(name = "test_id", nullable = false, length = 50)
    private String testId;
    @Column(name = "correct_answers")
    private Integer correctAnswers;
    @Column(name = "is_success")
    private Short isSuccess;
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;
    @Column(name = "correctAnswerPercentage")
    private Integer correctAnswerPercentage;
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private TestAttemptType type;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state", nullable = false)
    private TestAttemptState state;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testAttempt")
    private List<StudentTestAttemptDetail> details;
}
