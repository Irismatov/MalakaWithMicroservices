package com.malaka.aat.internal.dto.group;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupUpdateDto {
    @NotEmpty(message = "Students must be provided")
    @NotNull(message = "Students must be provided")
    @Size(min = 1, max = 1000, message = "Students must be between 1 and 1000")
    private List<String> students;
}
