package com.malaka.aat.external.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginOneIdRequest {
    @NotBlank(message = "Code must be provided")
    private String code;
}
