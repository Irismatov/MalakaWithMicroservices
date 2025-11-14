package com.malaka.aat.internal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "permissions")
@Entity(name = "permission")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE permissions SET is_deleted = 1")
public class Permission extends BaseEntity{
    @Id
    @Column(name = "id",  nullable = false)
    private Long id;
    @Column(name = "name",  nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private String name;
    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500)")
    private String description;

}
