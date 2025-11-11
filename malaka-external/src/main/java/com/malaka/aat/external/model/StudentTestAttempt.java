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
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;
    @Column(name = "topic_id")
    private String topicId;
    @Column(name = "test_id")
    private String testId;
    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;
    @Column(name = "is_success")
    private Short isSuccess;
}
