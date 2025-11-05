package com.malaka.aat.internal.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseFilterDto {
    @Schema(description = "Filter by course name", required = false, example = "Spring Boot Course")
    @Size(max = 200, message = "Name must be less than 200 characters")
    private String name;

    @Schema(description = "Filter by course state", required = false, example = "001")
    @Size(max = 5, message = "State must be less than 5 characters")
    private String state;

    @Schema(description = "Filter by course type ID", required = false, example = "1")
    private Long courseType;

    @Schema(description = "Filter by course format ID", required = false, example = "1")
    private Long courseFormat;

    @Schema(description = "Filter by course student type ID", required = false, example = "0")
    private Long courseStudentType;
}
