package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicUpdateDto {
    @NotEmpty(message = "Nomi bo'sh bo'lmasligi kerak")
    @Size(min = 5, max = 200, message = "Nomida kamida 5 ta eng ko'pi bilan 200 ta belgi bo'lishi kerak")
    private String name;
}
