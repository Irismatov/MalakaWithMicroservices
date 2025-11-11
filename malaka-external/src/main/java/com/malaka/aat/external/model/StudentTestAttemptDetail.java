package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "student_test_attempt_detail")
@SQLDelete(sql = "UPDATE test_attempt_detail SET is_deleted = 0 where id = ?")
@SQLRestriction(value = "is_deleted = 0")
public class StudentTestAttemptDetail extends BaseEntity{

    @Column(name = "id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "test_attempt_id", nullable = false)
    private StudentTestAttempt testAttempt;
    @Column(name = "question_id",  nullable = false, length = 50)
    private String questionId;
    @Column(name = "optionId", nullable = false, length = 50)
    private String optionId;
    @Column(name = "is_correct", columnDefinition = " SMALLINT DEFAULT 0", nullable = false)
    private Short isCorrect;

}
