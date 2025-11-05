package com.malaka.aat.internal.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "files")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE file SET is_deleted = 1 WHERE id = ?")
public class File extends BaseEntity {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "original_name", length = 1000)
    private String originalName;
    @Column(name = "path", nullable = false, length = 1000)
    private String path;
    @Column(name = "hash", nullable = false, length = 255)
    private String hash;
    @Column(name = "extension", nullable = false, length = 50)
    private String extension;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "file_size")
    private Long fileSize;
}
