package com.malaka.aat.external.dto.course.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CourseDto {
    private String id;
    private String name;
    private String description;
    private Integer moduleCount;
    private Long lang;
    private String state;
    private String imgUrl;
    private Long courseType;
    private Long courseFormat;
    private Long courseStudentType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<ModuleDto> modules;
}
