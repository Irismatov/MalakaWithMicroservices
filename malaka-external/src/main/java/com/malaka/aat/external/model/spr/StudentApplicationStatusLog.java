package com.malaka.aat.external.model.spr;

import com.malaka.aat.external.model.BaseEntity;
import com.malaka.aat.external.model.StudentApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "student_application_status_log")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_application_status_log SET is_deleted = 1 WHERE id = ?")
public class StudentApplicationStatusLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private StudentApplication application;
    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;
    @Column(name = "status_code",  nullable = false, length = 50)
    private String statusCode;
    @Column(name = "description", length = 10000)
    private String description;
    @Column(name = "order_number", nullable = false)
    private Integer order;
}
