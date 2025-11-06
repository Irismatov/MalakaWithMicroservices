package com.malaka.aat.external.dto.student_enrollment;

import com.malaka.aat.external.model.StudentEnrollmentDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentEnrollmentDto {
    private String id;
    private String groupId;
    private StudentEnrollmentDetailDto studentEnrollmentDetail;
}
