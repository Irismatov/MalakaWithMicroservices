package com.malaka.aat.external.model.spr;

import com.malaka.aat.external.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "staff_position")
public class StaffPositionSpr extends BaseEntity {
    @Id
    @Column(name = "id", length = 50)
    private Long id;
    @Column(name = "name", length = 200)
    private String name;
}
