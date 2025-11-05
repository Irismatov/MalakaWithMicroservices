package com.malaka.aat.internal.dto.spr.courseformat;

import com.malaka.aat.internal.model.spr.CourseFormatSpr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseFormatSprDto {
    private Long id;
    private String name;
    private String description;

    public CourseFormatSprDto(CourseFormatSpr courseFormatSpr) {
        if (courseFormatSpr != null) {
            this.id = courseFormatSpr.getId();
            this.name = courseFormatSpr.getName();
            this.description = courseFormatSpr.getDescription();
        }
    }
}
