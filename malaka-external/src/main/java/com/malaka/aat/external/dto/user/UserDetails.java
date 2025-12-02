package com.malaka.aat.external.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserDetails {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String pinfl;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String email;
    private String nationality;
    private Integer gender;
    private String workPlace;
    private String workPlaceDepartment;
    private String workPosition;
    private String licenseNumber;
    private String workCategory;
    private String passportNumber;
    private String passportGivenPlace;
    private List<String> roles;
    private String img;
}
