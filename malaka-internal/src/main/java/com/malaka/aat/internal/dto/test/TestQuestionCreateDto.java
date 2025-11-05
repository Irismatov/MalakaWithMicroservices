package com.malaka.aat.internal.dto.test;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestQuestionCreateDto {
    @NotBlank(message = "Question text must not be empty")
    @Size(min = 1, max = 3000, message = "The size of question text must be between 1 and 3000")
    private String questionText;
    @NotEmpty(message = "Test options must be provided")
    @Size(min = 3, max = 4, message = "The size of te questions must be between 3 and 4")
    private List<TestQuestionOptionCreateDto> options;
    @NotNull(message = "hasImage field must be provided")
    @Min(value = 0, message = "The hasImage field must be 0 or 1")
    @Max(value = 1, message = "The hasImage field must be 0 or 1")
    private Short hasImage;
    @Size(max = 500, message = "The imgUrl must be 500 at most")
    private String imgUrl;
}
