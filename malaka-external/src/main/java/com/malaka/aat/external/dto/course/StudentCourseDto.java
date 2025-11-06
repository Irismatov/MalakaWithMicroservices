package com.malaka.aat.external.dto.course;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourseDto {
    private String id;
    private String name;
    private String description;
    private String imgUrl;
    private Integer moduleCount;
    private Integer moduleStep;
    private Integer topicStep;
    private Integer contentStep;
    private Integer status;
    private String groupId;
}
