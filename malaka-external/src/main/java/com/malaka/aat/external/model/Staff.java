package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "staff")
@SQLRestriction("is_deleted=0")
@SQLDelete(sql = "UPDATE staff SET is_deleted = 0 WHERE id = ?")
public class Staff extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",  nullable = false, length = 50)
    private String id;
    @Column(name = "first_name", length = 200)
    private String firstName;
    @Column(name = "last_name", length = 200)
    private String lastName;
    @Column(name = "middle_name", length = 200)
    private String middleName;
    @Column(name = "info", length = 200)
    private String info;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file_id")
    private File imageFile;
}
