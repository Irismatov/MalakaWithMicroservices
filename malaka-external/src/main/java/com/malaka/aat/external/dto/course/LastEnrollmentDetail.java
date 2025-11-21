package com.malaka.aat.external.dto.course;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LastEnrollmentDetail {
    private String moduleId;
    private String topicId;
    private String contentId;
    private Integer contentType;
}
