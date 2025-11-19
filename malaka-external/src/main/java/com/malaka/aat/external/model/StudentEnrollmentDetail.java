package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Table(name = "student_enrollment_detail")
@Entity
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_enrollment_detail SET is_deleted = 1 WHERE id = ?")
public class StudentEnrollmentDetail extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_enrollment_id", nullable = false)
    private StudentEnrollment studentEnrollment;
    @Column(name = "module_id", length = 50)
    private String moduleId;
    @Column(name = "topic_id", length = 50)
    private String topicId;
    @Column(name = "content_id", length = 50)
    private String contentId;



    @Column(name = "module_step", columnDefinition = " INTEGER DEFAULT 1")
    private Integer moduleStep;
    @Column(name = "topic_step", columnDefinition = " INTEGER DEFAULT 1")
    private Integer topicStep;
    @Column(name = "content_step", columnDefinition = "INTEGER DEFAULT 1")
    private Integer contentStep;
    @Column(name = "is_active", columnDefinition = " SMALLINT DEFAULT 1")
    private Short isActive;
}
