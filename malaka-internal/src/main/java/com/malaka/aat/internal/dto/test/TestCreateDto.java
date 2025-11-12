package com.malaka.aat.internal.dto.test;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 1000, message = "Attempt limit must be between 1 and 1000")
    private Integer attemptLimit;
}