package com.malaka.aat.external.model;


import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "student_enrollment")
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_enrollment SET is_deleted = 1 WHERE id = ?")
public class StudentEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50)
    private String id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status",  nullable = false)
    private StudentEnrollmentStatus  status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
}
