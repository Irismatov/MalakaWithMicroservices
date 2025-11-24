package com.malaka.aat.internal.dto.course;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseNameAndIdDtoAndNewGroupName {
    private String id;
    private String name;
    private String groupName;

    public CourseNameAndIdDtoAndNewGroupName(String id, String name) {
        this.id = id;
        this.name = name;
        this.groupName = "1-guruh";
    }
}
