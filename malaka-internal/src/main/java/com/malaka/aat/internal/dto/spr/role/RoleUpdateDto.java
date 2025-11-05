package com.malaka.aat.internal.dto.spr.role;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateDto {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
