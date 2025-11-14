package com.malaka.aat.internal.model.spr;


import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "department_spr")
@Entity
@Getter
@Setter
public class DepartmentSpr extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 50)
    private String ID;
    @Column(length = 50, nullable = false, columnDefinition = "VARCHAR(50)")
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_head_id", referencedColumnName = "ID")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_spr_id", referencedColumnName = "ID")
    private FacultySpr facultySpr;


}
