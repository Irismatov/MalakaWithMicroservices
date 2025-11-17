package com.malaka.aat.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentApplicationDto {
    private String id;
    private String number;
    private String courseId;
    private String phone;
    private Integer applicationType;
    private Integer status;
    private String rejectionReason;
    private String fileId;
    private String courseName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    // Individual fields
    private StudentApplicationStudentInfo student;
    private String email;

    // Corporate fields
    private List<StudentApplicationStudentInfo> students;
    private String corporateName;
    private String stirNumber;
}
