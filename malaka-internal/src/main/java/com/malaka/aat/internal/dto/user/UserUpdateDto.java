package com.malaka.aat.internal.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserUpdateDto {
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    private String username;

    @Size(min = 1, max = 1000, message = "Password must be between 1 and 1000 characters")
    private String password;

    @Size(max = 200, message = "First name must be less than 200 characters")
    private String firstName;

    @Size(max = 200, message = "Last name must be less than 200 characters")
    private String lastName;

    @Size(max = 200, message = "Middle name must be less than 200 characters")
    private String middleName;

    @Size(max = 5, message = "Language must be less than 5 characters")
    private Long lang;

    @Size(max = 20, message = "PINFL must be less than 20 characters")
    private String pinfl;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    @Size(max = 50, message = "Group ID must be less than 50 characters")
    private String groupId;

    private Set<@Size(max = 50, message = "Role ID must be less than 50 characters") String> roleIds;
}
