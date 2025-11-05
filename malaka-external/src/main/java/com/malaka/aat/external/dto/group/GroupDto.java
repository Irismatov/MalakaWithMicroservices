package com.malaka.aat.external.dto.group;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupDto {
    private String id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String courseId;
    private String courseName;
    private Integer status;
}
