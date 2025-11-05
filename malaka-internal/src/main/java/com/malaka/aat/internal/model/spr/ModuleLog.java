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
@Table(name = "modul_log")
public class ModuleLog extends BaseEntity {

    @Id
    @Column(length = 50, nullable = false)
    private String id;

    @Column(length = 50)
    private String dId;

    @Column(length = 50)
    private String mId;

    @Column(length = 50)
    private String uId;

    @Column
    private Short mCd = 0;

    @Column
    private Short mAllCd = 0;

    @Column(length = 3)
    private String type;

}
