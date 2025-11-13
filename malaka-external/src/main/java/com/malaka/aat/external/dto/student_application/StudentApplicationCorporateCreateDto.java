package com.malaka.aat.external.dto.student_application;

import com.malaka.aat.external.model.StudentApplicationCorporate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class StudentApplicationCorporateCreateDto {
    @NotNull(message = "File must be provided")
    private MultipartFile file;
    @NotBlank(message = "Course id must be provided")
    private String courseId;
    @Size(min = 7, max = 30, message = "Phone number must have at least 7 numbers")
    @NotBlank(message = "Phone must be provided")
    private String phone;
    @NotEmpty(message = "Pinfl must be provdied")
    @NotNull(message = "Pinfl must be provided")
    private List<String> pinfls;
    @Size(min = 5, max = 100, message = "Corporate name must be between 5 and 100 characters")
    @NotBlank(message = "Corporate name must be provided")
    private String corporateName;
    @Size(min = 5, max = 50, message = "Stir number must be between 5 and 50 characters")
    @NotBlank(message = "Stir number must be provided")
    private String stirNumber;


    public static StudentApplicationCorporate mapDtoToEntity(StudentApplicationCorporateCreateDto dto) {
        StudentApplicationCorporate application = new StudentApplicationCorporate();
        application.setCourseId(dto.getCourseId());
        application.setPhone(dto.getPhone());
        application.setPinfls(dto.getPinfls());
        application.setCorporateName(dto.getCorporateName());
        application.setStirNumber(dto.getStirNumber());
        return application;
    }
}
