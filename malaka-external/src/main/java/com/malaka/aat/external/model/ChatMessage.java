package com.malaka.aat.external.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Table(name = "chat_message")
@Entity
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_enrollment_detail SET is_deleted = 1 WHERE id = ?")
public class ChatMessage {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
    @Column(name = "text", length = 1000)
    private String text;
    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;
}
