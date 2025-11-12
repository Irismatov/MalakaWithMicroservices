package com.malaka.aat.internal.dto.test;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCreateDto {
    @NotEmpty(message = "Questions must not be empty")
    @NotNull(message = "Questions must be provided")
    @Size(min = 10, max = 100, message = "The length of the questions must be between 10 and 100")
    private List<TestQuestionCreateDto> questions;
    @NotNull(message = "Attempt limit must be provided")
    @Min(value = 1, message = "Attempt limit must be at least 1")
    @Max(value = 1000, message = "Attempt limit must be at most 1000")
    private Integer attemptLimit;
}