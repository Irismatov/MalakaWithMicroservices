package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TopicFileUploadDto {

    @NotBlank(message = "Topic ID is required")
    private String topicId;

    @NotBlank(message = "File type is required")
    @Pattern(regexp = "^(CONTENT|LECTURE|TEST)$", message = "File type must be CONTENT, LECTURE, or TEST")
    private String fileType;

    @NotNull(message = "File is required")
    private MultipartFile file;
}
