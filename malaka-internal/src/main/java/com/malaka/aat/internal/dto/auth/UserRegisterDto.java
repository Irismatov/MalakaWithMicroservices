package com.malaka.aat.internal.dto.auth;


import com.malaka.aat.internal.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {
    @Size(min = 3, max = 50, message = "The length should be between 3 and 50")
    @NotBlank(message = "The field should not be blank or null")
    private String username;

    @Size(min = 8, max = 1000, message = "The length should be between 8 and 1000")
    @NotBlank(message = "The field should not be blank or null")
    private String password;

    @Size(max = 200, message = "The length should not exceed 200")
    private String firstName;

    @Size(max = 200, message = "The length should not exceed 200")
    private String lastName;

    @Size(max = 200, message = "The length should not exceed 200")
    private String middleName;

    @Min(0)
    private long lang;

    @Size(max = 20, message = "The length should not exceed 20")
    private String pinfl;

    @Size(max = 20, message = "The length should not exceed 20")
    private String phone;

    @Size(max = 50, message = "The length should not exceed 50")
    private String groupId;

    public static User mapDtoToEntity(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setMiddleName(userRegisterDto.getMiddleName());
        user.setPinfl(userRegisterDto.getPinfl());
        user.setPhone(userRegisterDto.getPhone());
        return user;
    }
}
