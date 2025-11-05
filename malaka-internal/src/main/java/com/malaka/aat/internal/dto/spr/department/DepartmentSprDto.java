package com.malaka.aat.internal.dto.spr.department;

import com.malaka.aat.internal.dto.spr.faculty.FacultySprDto;
import com.malaka.aat.internal.dto.user.UserDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.DepartmentSpr;
import com.malaka.aat.internal.model.spr.FacultySpr;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentSprDto {
    private String id;
    private String name;
    private UserDto head;
    private FacultySprDto faculty;

    public DepartmentSprDto(String id, String name, User head, FacultySpr faculty) {
        this.id = id;
        this.name = name;
        if (head != null) {
            this.head = new UserDto(head);
        }
        if (faculty != null) {
            this.faculty = new FacultySprDto(faculty);
        }
    }

    public DepartmentSprDto(DepartmentSpr departmentSpr) {
        if (departmentSpr != null) {
            this.id = departmentSpr.getID();
            this.name = departmentSpr.getName();
            if (departmentSpr.getUser() != null) {
                this.head = new UserDto(departmentSpr.getUser());
            }
            if (departmentSpr.getFacultySpr() != null) {
                this.faculty = new FacultySprDto(departmentSpr.getFacultySpr());
            }
        }
    }
}
