package com.malaka.aat.internal.dto.spr.coursetype;

import com.malaka.aat.internal.model.spr.CourseTypeSpr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseTypeSprDto {
    private Long id;
    private String name;
    private String description;

    public CourseTypeSprDto(CourseTypeSpr courseTypeSpr) {
        if (courseTypeSpr != null) {
            this.id = courseTypeSpr.getId();
            this.name = courseTypeSpr.getName();
            this.description = courseTypeSpr.getDescription();
        }
    }
}
