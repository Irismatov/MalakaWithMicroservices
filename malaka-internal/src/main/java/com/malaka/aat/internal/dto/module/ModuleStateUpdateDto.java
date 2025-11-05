package com.malaka.aat.internal.dto.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleStateUpdateDto {

    @NotBlank(message = "Module state is required")
    @Pattern(regexp = "^(001|002|003|004)$", message = "Module state must be one of: 001 (NEW), 002 (SENT), 003 (APPROVED), 004 (REJECTED)")
    private String state;
}
