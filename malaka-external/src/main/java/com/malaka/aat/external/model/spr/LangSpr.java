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


@Entity
@Table(name = "lang_spr")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE lang_spr SET is_deleted = 1 WHERE id = ?")
public class LangSpr extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;
}
