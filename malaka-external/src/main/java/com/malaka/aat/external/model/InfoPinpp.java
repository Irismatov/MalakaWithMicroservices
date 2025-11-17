package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLUpdate;

@Entity
@Table(name = "info_pinpp")
@Getter
@Setter
@SQLRestriction("is_deleted=0")
@SQLDelete(sql = "UPDATE info_pinpp SET is_deleted=1 WHERE id = ?")
public class InfoPinpp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 50)
    private String id;
    @Column(name = "pinpp", length = 50, nullable = false)
    private String pinpp;
    @Column(name = "first_name", length = 300)
    private String firstName;
    @Column(name = "last_name", length = 300)
    private String lastName;
    @Column(name = "middle_name", length = 300)
    private String middleName;

}
