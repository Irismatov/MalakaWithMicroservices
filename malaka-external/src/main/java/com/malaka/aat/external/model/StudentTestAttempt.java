package com.malaka.aat.external.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
    @Column(name = "topic_id", nullable = false, length = 50)
    private String topicId;
    @Column(name = "test_id", nullable = false, length = 50)
    private String testId;
    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;
    @Column(name = "is_success", nullable = false)
    private Short isSuccess;
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;
    @Column(name = "correctAnswerPercentage", nullable = false)
    private Integer correctAnswerPercentage;
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
}
