package com.malaka.aat.external.dto.student_application;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentApplicationUpdateDto {
    @Min(value = 1, message = "Status must be at least 1")
    @Max(value = 2, message = "Status must be at most 2")
    private Integer status;
    private String description;
}
