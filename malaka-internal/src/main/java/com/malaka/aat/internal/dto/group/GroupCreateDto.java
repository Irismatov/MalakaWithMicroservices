package com.malaka.aat.internal.dto.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GroupCreateDto {
    @NotNull(message = "Start date must be provided")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @NotNull(message = "End date must be provided")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @NotEmpty(message = "Students must be provided")
    @NotNull(message = "Students must be provided")
    @Size(min = 1, max = 1000, message = "Students must be between 1 and 1000")
    private List<String> students;
    @Size(min = 5, max = 50, message = "Course must have 5-50 characters")
    @NotEmpty(message = "Course id must be provided")
    private String courseId;
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;
}
