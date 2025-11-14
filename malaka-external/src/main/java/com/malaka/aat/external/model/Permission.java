package com.malaka.aat.external.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLUpdate;

@Table(name = "permissions")
@Entity(name = "permission")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLUpdate(sql = "UPDATE permission SET is_deleted = 1 WHERE id = ?")
public class Permission extends BaseEntity {
    @Id
    @Column(name = "id",  nullable = false)
    private Long id;
    @Column(name = "name",  nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private String name;
    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500)")
    private String description;

}
