package com.malaka.aat.external.dto.course.external;

import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String imgUrl;
    private Long courseType;
    private Long courseFormat;
    private Long courseStudentType;
    private List<ModuleDto> modules;
    private StudentEnrollmentDetailDto studentEnrollment;
}
