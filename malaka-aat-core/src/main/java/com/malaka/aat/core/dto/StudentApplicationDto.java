package com.malaka.aat.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for student application data to be shared between services.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentApplicationDto {
    private String id;
    private String courseId;
    private String phone;
    private Integer applicationType; // "INDIVIDUAL" or "CORPORATE"
    private Integer status; // Application status code
    private String fileId;
    private LocalDateTime createdDate;

    // Individual fields
    private String pinfl;
    private String email;

    // Corporate fields
    private List<String> pinfls;
    private String corporateName;
    private String stirNumber;
}
