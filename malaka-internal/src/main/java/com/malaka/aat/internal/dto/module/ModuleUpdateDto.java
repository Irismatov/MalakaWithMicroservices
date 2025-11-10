package com.malaka.aat.internal.dto.module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleUpdateDto {

    @NotNull(message = "Name must be provided")
    @Size(min = 3, max = 50, message = "Module name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Order must be provided")
    @Min(value = 1, message = "Order must be at least 1")
    private Integer order;

    @NotNull(message = "Topic count must be provided")
    @Min(value = 1, message = "Topic count must be at least 1")
    private Integer topicCount;

    @NotNull(message = "Teacher ID must be provided")
    @Size(min = 3, max = 50, message = "Teacher ID must be between 3 and 50 characters")
    private String teacherId;

    @NotNull(message = "Faculty ID must be provided")
    @Size(max = 50, message = "Faculty ID must not exceed 50 characters")
    private String facultyId;

    @NotNull(message = "Department ID must be provided")
    @Size(max = 50, message = "Department ID must not exceed 50 characters")
    private String departmentId;


}
