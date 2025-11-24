package com.malaka.aat.internal.dto.course;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseNameAndIdDto {
    private String id;
    private String name;

    public CourseNameAndIdDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
