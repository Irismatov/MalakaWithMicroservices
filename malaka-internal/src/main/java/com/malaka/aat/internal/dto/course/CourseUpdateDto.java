package com.malaka.aat.internal.dto.course;

import com.malaka.aat.internal.model.Course;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CourseUpdateDto {
    @Size(min = 3,  max = 200, message = "Name must contain between 3 and 200 characters")
    private String name;
    @Min(0)
    @Max(2)
    private Long lang;
    @Size(min = 3, max = 1500, message = "Description must contain between 3 and 1500 characters")
    private String description;
    private Long courseType;
    private Long courseFormat;
    private Long courseStudentType;
    private MultipartFile file;

    public static void setFieldsToEntity(Course course, CourseUpdateDto dto) {
        if (dto.getName() != null) {
            course.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        // courseType, courseFormat, and courseStudentType will be set in service layer using IDs if provided
    }
}
