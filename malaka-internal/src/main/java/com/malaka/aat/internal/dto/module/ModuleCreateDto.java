package com.malaka.aat.internal.dto.module;

import com.malaka.aat.internal.model.Module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleCreateDto {
    @NotBlank(message = "Module name must be provided")
    @Size(min = 3, max = 200, message = "Module name must be between 3 and 200 characters")
    private String name;

    @NotNull(message = "Course ID must be provided")
    @Size(min = 3, max = 50, message = "Course id must have between 3 and 50 characters")
    private String courseId;

    @NotNull(message = "Order must be provided")
    @Min(value = 1, message = "Order must be at least 1")
    private Integer order;

    @NotNull(message = "Teacher ID must be provided")
    @Size(min = 3, max = 50, message = "Teacher id must have between 3 and 50 characters")
    private String teacherId;

    @NotNull(message = "TopicCount must be provided")
    private Integer topicCount;

    public static Module mapDtoToEntity(ModuleCreateDto dto) {
        Module module = new Module();
        module.setName(dto.getName());
        module.setTopicCount(dto.getTopicCount());
        return module;
    }
}
