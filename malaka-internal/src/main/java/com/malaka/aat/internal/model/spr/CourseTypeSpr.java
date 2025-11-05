package com.malaka.aat.internal.model.spr;

import com.malaka.aat.internal.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "course_type_spr")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE course_type_spr SET is_deleted = 1 WHERE id = ?")
public class CourseTypeSpr extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(length = 50, nullable = false, unique = true, columnDefinition = "VARCHAR(50) CCSID 1208")
    private String name;

    @Column(length = 200)
    private String description;
}
