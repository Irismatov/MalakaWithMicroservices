package com.malaka.aat.internal.dto.course;


import com.malaka.aat.internal.model.Course;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CourseCreateDto {
    @NotBlank(message = "Name must be provided")
    @Size(min = 3,  max = 200, message = "Name must contain between 3 and 200 characters")
    private String name;
    @Min(0)
    @Max(10)
    @NotNull
    private Long lang;
    @Size(min = 3, max = 1500, message = "Description must contain between 3 and 1500 characters")
    private String description;
    @NotNull(message = "Module count must be provided")
    @Min(value = 1, message = "Module count must be at least 1")
    @Max(value = 100, message = "Module count must be at most 100")
    private Integer moduleCount;
    @NotNull(message = "Course type must be provided")
    private Long courseType;
    @NotNull(message = "Course format must be provided")
    private Long courseFormat;
    @NotNull(message = "Course student type must be provided")
    private Long courseStudentType;
    @NotNull(message = "File must be provided")
    private MultipartFile file;

    public static Course mapDtoToEntity(CourseCreateDto dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setModuleCount(dto.getModuleCount());
        return course;
    }
}
