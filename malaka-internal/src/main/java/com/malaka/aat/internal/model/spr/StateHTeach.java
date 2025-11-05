package com.malaka.aat.internal.model.spr;

import com.malaka.aat.internal.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "state_h_teach")
public class StateHTeach extends BaseEntity {

    @Id
    @Column(length = 50, nullable = false)
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

    @Column(length = 3, columnDefinition = "varchar(3) default '001'")
    private String histSrno = "001";

    @Column(columnDefinition = "smallint default 110")
    private Short stateCd = 110;

    @Column(length = 20)
    private String uncodId;

    @Column(length = 2)
    private String darsType;

}
