package com.malaka.aat.internal.dto.spr.role;

import com.malaka.aat.internal.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
    private String id;
    private String name;
    private String description;

    public RoleDto(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public RoleDto(Role role) {
        if (role != null) {
            this.id = role.getId();
            this.name = role.getName();
            this.description = role.getDescription();
        }
    }
}
