package com.malaka.aat.external.dto.course.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ModuleDto {
    private String id;
    private String name;
    private Integer topicCount;
    private Integer order;
    private String teacherId;
    private String teacherName;
    private String departmentId;
    private String departmentName;
    private String facultyId;
    private String facultyName;
    private String state;
    private String courseId;
    private String courseName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<TopicDto> topics;
}
