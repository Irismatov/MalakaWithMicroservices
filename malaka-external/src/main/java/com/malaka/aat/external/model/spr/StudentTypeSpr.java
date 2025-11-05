package com.malaka.aat.external.model.spr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "student_type_spr")
public class StudentTypeSpr {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(length = 1000)
    private String description;
}
