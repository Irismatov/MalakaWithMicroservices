package com.malaka.aat.external.dto.enrollment;

import com.malaka.aat.external.model.StudentEnrollmentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentEnrollmentDetailDto {
    private Integer moduleStep;
    private Integer topicStep;
    private Integer contentStep;

    public StudentEnrollmentDetailDto(StudentEnrollmentDetail detail) {
        this.moduleStep = detail.getModuleStep();
        this.topicStep = detail.getTopicStep();
        this.contentStep = detail.getContentStep();
    }
}
