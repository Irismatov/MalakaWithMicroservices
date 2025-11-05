package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TopicContentUploadDto {
    private MultipartFile file;
    @NotNull(message = "Content type must be provided")
    @Min(0)
    @Max(2)
    private Integer contentType;
}
