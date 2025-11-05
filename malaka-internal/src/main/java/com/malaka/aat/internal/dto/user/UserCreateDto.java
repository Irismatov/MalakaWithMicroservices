package com.malaka.aat.internal.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "Username must be provided")
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    private String username;

    @NotBlank(message = "Password must be provided")
    @Size(min = 1, max = 1000, message = "Password must be between 1 and 1000 characters")
    private String password;

    @Size(max = 200, message = "First name must be less than 200 characters")
    private String firstName;

    @Size(max = 200, message = "Last name must be less than 200 characters")
    private String lastName;

    @Size(max = 200, message = "Middle name must be less than 200 characters")
    private String middleName;

    private short lang;

    @Size(max = 20, message = "PINFL must be less than 20 characters")
    private String pinfl;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    @Size(max = 50, message = "Group ID must be less than 50 characters")
    private String groupId;

    @NotEmpty(message = "At least one role must be provided")
    private Set<@NotBlank(message = "Role ID cannot be blank") @Size(max = 50, message = "Role ID must be less than 50 characters") String> roleIds;
}
