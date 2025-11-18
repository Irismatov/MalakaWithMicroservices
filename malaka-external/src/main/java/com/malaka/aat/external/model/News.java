package com.malaka.aat.external.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLUpdate;

@Getter
@Setter
@Entity
@Table(name = "news")
@SQLRestriction("is_deleted=0 ")
@SQLDelete(sql = "UPDATE news SET is_deleted=1 WHERE id = ?")
public class News extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",  nullable = false)
    private String id;
    @Column(name = "title", length = 50)
    private String title;
    @Column(name = "text", length = 1000)
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file_id")
    private File imageFile;
}
