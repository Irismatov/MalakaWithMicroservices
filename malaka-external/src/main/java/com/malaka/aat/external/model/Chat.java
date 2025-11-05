package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Table(name = "chat")
@Entity
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE chat SET is_deleted = 1 WHERE id = ?")
public class Chat {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

}
