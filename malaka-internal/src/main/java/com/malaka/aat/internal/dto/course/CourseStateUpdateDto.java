package com.malaka.aat.internal.dto.course;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseStateUpdateDto {
    @NotNull(message = "State can't be null")
    @Size(min = 2, max = 5, message = "Length of the state must be between 2 and 5")
    private String state;
    @Size(min = 3, max = 1000, message = "Length of description must be between 3 and 1000")
    private String description;
}
