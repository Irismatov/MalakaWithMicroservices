package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicCreateDto {

    @NotBlank(message = "Topic name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;


    @NotNull(message = "Order is required")
    @Min(value = 0, message = "Order must be at least 0")
    private Integer order;
}
