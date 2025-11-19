package com.malaka.aat.external.dto.course.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentCourseDto {
    private String id;
    private String name;
    private String description;
    private String imgUrl;
    private Integer moduleCount;
    private String groupId;
    private String groupName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Integer groupStatus;
    private int isStarted;
    private int isFinished;
    private int isExpired;
}
