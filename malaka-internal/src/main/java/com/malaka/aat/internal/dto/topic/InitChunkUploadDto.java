package com.malaka.aat.internal.dto.topic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitChunkUploadDto {
    @NotBlank(message = "Original filename must be provided")
    private String originalFileName;

    @NotNull(message = "Total file size must be provided")
    @Min(1)
    private Long totalFileSize;

    @NotNull(message = "Total chunks must be provided")
    @Min(1)
    private Integer totalChunks;

    @NotNull(message = "Content type must be provided")
    @Min(0)
    @Max(2)
    private Integer contentType;

    private String mimeType;
}
