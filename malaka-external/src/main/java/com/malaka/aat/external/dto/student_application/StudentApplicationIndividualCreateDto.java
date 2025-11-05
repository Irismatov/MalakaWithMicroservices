package com.malaka.aat.external.dto.student_application;

import com.malaka.aat.external.model.StudentApplicationIndividual;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StudentApplicationIndividualCreateDto {
    @NotNull(message = "File must be provided")
    private MultipartFile file;
    @Size(min = 5, max = 50, message = "Course id must be between 5-50 letters")
    @NotBlank(message = "Course id must be provided")
    private String courseId;
    @Size(min = 7, max = 30, message = "Phone number must have at least 7 numbers")
    @NotBlank(message = "Phone must be provided")
    private String phone;
    @Size(min = 14, max = 14, message = "Pinfl must have 14 characters")
    @NotBlank(message = "Pinfl must be provided")
    private String pinfl;
    @Size(min = 5, max = 50, message = "Email must have 5-50 characters")
    @NotBlank(message = "Email must be provided")
    private String email;


    public static StudentApplicationIndividual mapDtoToEntity(StudentApplicationIndividualCreateDto dto) {
        StudentApplicationIndividual application = new StudentApplicationIndividual();
        application.setCourseId(dto.getCourseId());
        application.setPinfl(dto.getPinfl());
        application.setEmail(dto.getEmail());
        application.setPhone(dto.getPhone());
        return application;
    }
}
