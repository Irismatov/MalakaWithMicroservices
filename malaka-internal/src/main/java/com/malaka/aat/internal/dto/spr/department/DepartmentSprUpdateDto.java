package com.malaka.aat.internal.dto.spr.department;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentSprUpdateDto {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50")
    private String name;
    @Size(min = 2, max = 50, message = "Head id must be between 2 and 50")
    private String headId;
    @Size(min = 2, max = 50, message = "Faculty id must be between 2 and 50")
    private String facultyId;
}
