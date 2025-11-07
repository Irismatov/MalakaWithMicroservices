package com.malaka.aat.internal.model.spr;

import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.model.Module;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "state_h_met")
public class StateHMet extends BaseEntity {

    @Id
    @Column(length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String bodyId;

    @Column(length = 3, columnDefinition = "varchar(3) default '001'")
    private String imexTpcd = "001";

    @Column(length = 20)
    private String aplcPnfl;

    @Column(length = 1200, columnDefinition = "VARCHAR(1200) CCSID 1208")
    private String aplcRppnNm;

    @Column(length = 50)
    private String aplcTelno;

    @Column(length = 3600, columnDefinition = "VARCHAR(3600) CCSID 1208")
    private String descriptions;

    @Column(length = 3)
    private Integer histSrn;

    @Column(length = 20)
    private String uncodId;

    @Column(length = 2)
    private String darsType;

    @Column(name = "state_nm", length = 500, nullable = false, columnDefinition = "VARCHAR(500) CCSID 1208")
    private String stateNm;

    @Column(name = "state_cd", nullable = false, length = 10)
    private String stateCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corse_id", referencedColumnName = "id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

}
