package com.malaka.aat.external.model;


import com.malaka.aat.external.config.DefaultLangEntityListener;
import com.malaka.aat.external.model.spr.LangSpr;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, DefaultLangEntityListener.class})
public abstract class BaseEntity {

    @CreatedBy
    @Column(name = "insuser", length = 50, updatable = false)
    private String insuser;

    @LastModifiedBy
    @Column(name = "upduser", length = 50)
    private String upduser;

    @CreatedDate
    @Column(name = "instime", columnDefinition = " timestamp default current_timestamp", updatable = false)
    private LocalDateTime instime;

    @LastModifiedDate
    @Column(name = "updtime", columnDefinition = " timestamp default current_timestamp")
    private LocalDateTime updtime;

    @Column(name = "is_deleted", columnDefinition = " SMALLINT default 0")
    private short isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id", referencedColumnName = "id")
    private LangSpr lang;

}
