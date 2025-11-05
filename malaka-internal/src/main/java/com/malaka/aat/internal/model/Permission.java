package com.malaka.aat.internal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "permissions")
@Entity(name = "permission")
@Getter
@Setter
public class Permission {
    @Id
    @Column(name = "id",  nullable = false)
    private Long id;
    @Column(name = "name",  nullable = false, length = 50, columnDefinition = "VARCHAR(50) CCSID 1208")
    private String name;
    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500) CCSID 1208")
    private String description;

}
