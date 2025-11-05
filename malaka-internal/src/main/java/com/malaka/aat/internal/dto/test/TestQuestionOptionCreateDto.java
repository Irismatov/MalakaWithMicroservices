package com.malaka.aat.internal.dto.test;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestQuestionOptionCreateDto {
    @NotBlank(message = "optionText must be provided")
    @Size(min = 1, max = 500, message = "The length of optionText must be between 1 and 500")
    private String optionText;
    @NotNull(message = "isCorrect must be provided")
    @Min(value = 0, message = "isCorrect field can only be 0 or 1")
    @Max(value = 1, message = "isCorrect field can only be 0 or 1")
    private Short isCorrect;
    @NotNull(message = "hasImage field must be provided")
    @Min(value = 0, message = "The hasImage field must be 0 or 1")
    @Max(value = 1, message = "The hasImage field must be 0 or 1")
    private Short hasImage;
    @Size(max = 500, message = "The imgUrl must be 500 at most")
    private String imgUrl;
}
