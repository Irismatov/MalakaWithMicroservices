package com.malaka.aat.internal.dto.spr.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleListDto {
    private String id;
    private String name;
    private String description;

    public RoleListDto(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
