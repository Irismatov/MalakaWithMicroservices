package com.malaka.aat.internal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Table(name = "role")
@Entity(name = "role")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE role SET is_deleted = 1 WHERE id = ?")
public class Role extends BaseEntity {
    @Id
    @Column(name = "id", length = 50,nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 50, nullable = false, columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500)")
    private String description;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

}
