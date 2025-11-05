package com.malaka.aat.internal.dto.module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleUpdateDto {

    @Size(min = 3, max = 200, message = "Module name must be between 3 and 200 characters")
    private String name;

    @Min(value = 1, message = "Topic count must be at least 1")
    private Integer topicCount;

    @Size(min = 3, max = 50, message = "Teacher ID must be between 3 and 50 characters")
    private String teacherId;

    @Size(max = 50, message = "Faculty ID must not exceed 50 characters")
    private String facultyId;

    @Size(max = 50, message = "Department ID must not exceed 50 characters")
    private String departmentId;
}
