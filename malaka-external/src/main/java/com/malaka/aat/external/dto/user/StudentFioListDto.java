package com.malaka.aat.external.dto.user;

import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentFioListDto {
    private String id;
    private String fio;


    public StudentFioListDto(Student student) {
        this.id = student.getId();
        StringBuilder fio = new StringBuilder();
        User user = student.getUser();
        if (user.getLastName() != null) {
            fio.append(user.getLastName());
        }
        if (user.getFirstName() != null) {
            fio.append(" ").append(user.getFirstName());
        }
        if (user.getMiddleName() != null) {
            fio.append(" ").append(user.getMiddleName());
        }
    }
}
