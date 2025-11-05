package com.malaka.aat.internal.dto.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
    @Size(min = 3, max = 50, message = "The length should be between 3 and 50")
    @NotBlank(message = "The field should not be blank or null")
    private String username;
    @Size(min = 8, max = 50, message = "The length should be between 8 and 50")
    @NotBlank(message = "The field should not be blank or null")
    private String password;
}