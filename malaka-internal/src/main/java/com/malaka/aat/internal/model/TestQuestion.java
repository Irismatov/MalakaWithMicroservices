package com.malaka.aat.internal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Table(name = "test_question")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE test_question SET is_deleted = 1 WHERE id = ?")
public class TestQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "question_text", length = 3000, nullable = false, columnDefinition = "VARCHAR(3000) CCSID 1208")
    private String questionText;

    @Column(name = "has_image")
    private Short hasImage = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_image_id", referencedColumnName = "id")
    private File questionImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id", nullable = false)
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options;
}
