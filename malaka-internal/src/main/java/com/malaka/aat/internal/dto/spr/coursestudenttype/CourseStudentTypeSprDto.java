package com.malaka.aat.internal.dto.spr.coursestudenttype;

import com.malaka.aat.internal.model.spr.CourseStudentTypeSpr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseStudentTypeSprDto {
    private Long id;
    private String name;
    private String description;

    public CourseStudentTypeSprDto(CourseStudentTypeSpr courseStudentTypeSpr) {
        if (courseStudentTypeSpr != null) {
            this.id = courseStudentTypeSpr.getId();
            this.name = courseStudentTypeSpr.getName();
            this.description = courseStudentTypeSpr.getDescription();
        }
    }
}
