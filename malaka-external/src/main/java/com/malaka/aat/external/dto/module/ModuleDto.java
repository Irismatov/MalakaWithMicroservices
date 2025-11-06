package com.malaka.aat.external.dto.module;

import com.malaka.aat.external.dto.topic.TopicDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModuleDto {
    private String id;
    private String name;
    private Integer topicCount;
    private Integer order;
    private String teacherName;
    private String departmentName;
    private String facultyName;
    private String courseId;
    private String courseName;
    private List<TopicDto> topics;
}
