package com.malaka.aat.internal.model.spr;


import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "faculty_spr")
@Getter
@Setter
public class FacultySpr extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50)
    private String id;
    @Column(length = 50, nullable = false, columnDefinition = "VARCHAR(50) CCSID 1208")
    private String name;
    @OneToOne
    @JoinColumn(name = "faculty_head_id", referencedColumnName = "ID")
    private User head;
}
