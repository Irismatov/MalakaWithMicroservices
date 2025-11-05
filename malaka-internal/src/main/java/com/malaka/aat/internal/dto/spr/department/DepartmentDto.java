package com.malaka.aat.internal.dto.spr.department;

import com.malaka.aat.internal.dto.user.UserDto;
import com.malaka.aat.internal.model.spr.DepartmentSpr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DepartmentDto {
    private String id;
    private String name;
    private UserDto head;

    public DepartmentDto(DepartmentSpr departmentSpr) {
        if (departmentSpr != null) {
            this.id = departmentSpr.getID();
            this.name = departmentSpr.getName();
            this.head = new UserDto(departmentSpr.getUser());
        }
    }
}
