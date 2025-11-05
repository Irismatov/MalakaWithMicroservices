package com.malaka.aat.external.model;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "student_application_individual")
@DiscriminatorValue("INDIVIDUAL")
public class StudentApplicationIndividual extends StudentApplication{
    @Column(name = "pinfl", length = 20)
    private String pinfl;
    @Column(name = "email", length = 50)
    private String email;
}
