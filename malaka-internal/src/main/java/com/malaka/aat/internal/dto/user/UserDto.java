package com.malaka.aat.internal.dto.user;

import com.malaka.aat.internal.model.Role;
import com.malaka.aat.internal.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private Long lang;
    private String pinfl;
    private String phone;
    private Set<String> roleNames;


    public UserDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.middleName = user.getMiddleName();
            this.lang = user.getLang().getId();
            this.pinfl = user.getPinfl();
            this.phone = user.getPhone();
            if (user.getRoles() != null) {
                this.roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
            }
        }
    }

    public static UserDto mapEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMiddleName(user.getMiddleName());
        userDto.setLang(user.getLang().getId());
        userDto.setPinfl(user.getPinfl());
        userDto.setPhone(user.getPhone());
        if (user.getRoles() != null) {
            userDto.setRoleNames(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        return userDto;
    }
}
