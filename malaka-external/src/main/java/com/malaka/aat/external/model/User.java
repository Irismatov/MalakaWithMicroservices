package com.malaka.aat.external.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "USER")
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE users SET is_deleted = 1 WHERE id = ?")
public class User extends BaseEntity {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "username", columnDefinition = "VARCHAR(50)")
    @Size(min = 1, max = 50, message = "Логин киритилмаган")
    private String username;
    @Column(name = "user_password", columnDefinition = "VARCHAR(1000)")
    @Size(min = 1, message = "Илтимос паролни киритинг")
    private String password;
    @Column(name = "first_name", length = 200, columnDefinition = "VARCHAR(200)")
    private String firstName;
    @Column(name = "last_name", length = 200, columnDefinition = "VARCHAR(200)")
    private String lastName;
    @Column(name = "middle_name", length = 200, columnDefinition = "VARCHAR(200)")
    private String middleName;
    @Column(name = "pinfl", length = 20)
    private String pinfl;
    @Column(name = "phone", length = 20)
    private String phone;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
