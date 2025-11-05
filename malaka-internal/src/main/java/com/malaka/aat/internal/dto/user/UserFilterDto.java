package com.malaka.aat.internal.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDto {
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    @Size(max = 200, message = "First name must be less than 200 characters")
    private String firstName;

    @Size(max = 200, message = "Last name must be less than 200 characters")
    private String lastName;

    @Size(max = 200, message = "Middle name must be less than 200 characters")
    private String middleName;

    @Size(max = 20, message = "PINFL must be less than 20 characters")
    private String pinfl;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    @Size(max = 50, message = "Group ID must be less than 50 characters")
    private String groupId;

    @Size(max = 50, message = "Role ID must be less than 50 characters")
    private String roleId;
}
