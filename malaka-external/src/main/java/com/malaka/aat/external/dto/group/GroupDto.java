package com.malaka.aat.external.dto.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GroupDto {
    private String id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String courseId;
    private String courseName;
    private Integer status;
    private List<Student> students;

    @Getter
    @Setter
    public static class Student {
        private String id;
        private String fio;
    }
}
