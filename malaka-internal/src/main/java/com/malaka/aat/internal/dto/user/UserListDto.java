package com.malaka.aat.internal.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private Long lang;
    private String pinfl;
    private String phone;
    private String groupId;
    private String groupName;
    private String roleNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
