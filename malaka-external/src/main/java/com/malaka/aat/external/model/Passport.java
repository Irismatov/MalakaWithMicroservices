package com.malaka.aat.external.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLUpdate;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "passports")
@SQLRestriction("is_deleted=0 ")
@SQLUpdate(sql = "UPDATE passports SET is_deleted=1 WHERE id = ?")
public class Passport extends BaseEntity{
    @Id
    @Column(name = "id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "ser_number", length = 50, nullable = false)
    private String serNumber;
    @Temporal(TemporalType.DATE)
    @Column(name = "given_date")
    private LocalDate givenDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "status")
    private Integer status;
    @Column(name = "type")
    private String type;
    @Column(name = "doc_given_place", length = 1000)
    private String docGivenPlace;
    @Column(name = "doc_given_place_id")
    private Long docGivenPlaceId;
    @Column(name = "is_current", columnDefinition = " SMALLINT DEFAULT 0")
    private Short isCurrent;
    @ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
