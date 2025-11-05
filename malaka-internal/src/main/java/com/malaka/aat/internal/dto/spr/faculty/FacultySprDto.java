package com.malaka.aat.internal.dto.spr.faculty;


import com.malaka.aat.internal.dto.user.UserDto;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.FacultySpr;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultySprDto {
    private String id;
    private String name;
    private UserDto head;

    public FacultySprDto(String id, String name, User head) {
        this.id = id;
        this.name = name;
        if (head != null) {
            this.head = new UserDto(head);
        }
    }

    public FacultySprDto(FacultySpr facultySpr) {
        if  (facultySpr != null) {
            this.id = facultySpr.getId();
            this.name = facultySpr.getName();
            this.head = new UserDto(facultySpr.getHead());
        }
    }
}
