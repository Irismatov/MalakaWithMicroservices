package com.malaka.aat.internal.dto.spr.faculty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultySprCreateDto {
    @NotBlank(message = "Name must be provided")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50")
    private String name;
    @Size(min = 2, max = 50, message = "Head id must be between 2 and 50")
    private String headId;
}
