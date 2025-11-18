package com.malaka.aat.external.dto.user;

import com.malaka.aat.external.model.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentFioListDto {
    private String id;
    private String fio;


    public StudentFioListDto(Student student) {
        this.id = student.getId();
        this.fio = student.getUser().getFirstName() + " "  + student.getUser().getLastName();
    }
}
