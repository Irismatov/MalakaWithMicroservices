package com.malaka.aat.internal.model.spr;

import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.Module;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Getter
@Setter
@Entity
@Table(name = "state_h_module")
public class StateHModule extends BaseEntity {

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

    @Column(length = 1200)
    private String aplcRppnNm;

    @Column(length = 50)
    private String aplcTelno;

    @Column(length = 3600)
    private String descriptions;

    @Column(name = "hist_srno")
    private Integer histSrno;


    @Column(name = "state_nm")
    private String stateNm;

    @Column(name = "state_cd")
    private String stateCd;

    @Column(length = 20)
    private String uncodId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "module_id")
    private Module module;

}
