package com.malaka.aat.internal.model;

import com.malaka.aat.internal.enumerators.topic.TopicContentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "topic")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE topic SET is_deleted = 1 WHERE id = ?")
public class Topic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "name", length = 200, nullable = false, columnDefinition = "VARCHAR(200)")
    private String name;

    @Column(name = "topic_order")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module;

    // Video or Audio file
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_file_id", referencedColumnName = "id")
    private File contentFile;

    @Enumerated(EnumType.ORDINAL)
    private TopicContentType contentType;

    // only pdf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_file_id", referencedColumnName = "id")
    private File lectureFile;

    // only pdf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_file_id", referencedColumnName = "id")
    private File presentationFile;

    @OneToOne(fetch = FetchType.LAZY)
    private Test test;
}
