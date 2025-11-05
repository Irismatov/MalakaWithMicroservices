package com.malaka.aat.internal.dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChunkUploadResponseDto {
    private String uploadId;
    private String message;
    private Integer totalChunks;
    private Integer uploadedChunks;
}
