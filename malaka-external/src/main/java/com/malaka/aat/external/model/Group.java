package com.malaka.aat.external.model;

import com.malaka.aat.external.enumerators.group.GroupStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Table(name = "groups")
@Entity
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE groups SET is_deleted = 1 WHERE id = ?")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50)
    private String id;
    @Column(name = "order_number")
    private Integer order;
    @Column(name = "course_id",  length = 50, nullable = false)
    private String courseId;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    @Enumerated(EnumType.ORDINAL)
    private GroupStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students;
}
