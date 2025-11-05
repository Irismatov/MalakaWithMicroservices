package com.malaka.aat.internal.dto.spr.coursetype;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseTypeSprUpdateDto {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
}
