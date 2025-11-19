package com.malaka.aat.external.dto.topic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicDto {
    private String id;
    private String name;
    private String moduleId;
    private String moduleName;
    private Integer order;
    private String contentFileId;
    private String lectureFileId;
    private String presentationFileId;
    private String testId;
}
