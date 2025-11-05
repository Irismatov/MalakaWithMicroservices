package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TopicTestFileUploadDto {

    @NotNull(message = "Test file is required")
    private MultipartFile testFile;
}
