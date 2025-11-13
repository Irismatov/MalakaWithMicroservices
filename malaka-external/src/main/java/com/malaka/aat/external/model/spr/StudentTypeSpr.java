package com.malaka.aat.external.model.spr;

import com.malaka.aat.external.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Setter
@Getter
@Entity
@Table(name = "student_type_spr")
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_type_spr SET is_deleted = 1 WHERE id = ?")
public class StudentTypeSpr extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(length = 1000)
    private String description;
}
