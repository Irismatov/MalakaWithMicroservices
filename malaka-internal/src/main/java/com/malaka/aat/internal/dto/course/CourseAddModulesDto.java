package com.malaka.aat.internal.dto.course;

import com.malaka.aat.internal.dto.module.ModuleItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseAddModulesDto {
    @NotEmpty(message = "Modules list must not be empty")
    @Valid
    private List<ModuleItemDto> modules;
}
