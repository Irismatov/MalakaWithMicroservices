package com.malaka.aat.internal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "question_option")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE question_option SET is_deleted = 1 WHERE id = ?")
public class QuestionOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "option_text", length = 1000, columnDefinition = "VARCHAR(1000)")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Short isCorrect = 0;

    @Column(name = "has_image")
    private Short hasImage = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file_id", referencedColumnName = "id")
    private File imageFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private TestQuestion question;
}
