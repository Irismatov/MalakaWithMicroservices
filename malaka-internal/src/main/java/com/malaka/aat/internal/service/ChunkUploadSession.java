package com.malaka.aat.internal.service;

import com.malaka.aat.internal.enumerators.topic.TopicContentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Session metadata for tracking chunk uploads
 */
@Getter
@Setter
public class ChunkUploadSession {
    private String uploadId;
    private String topicId;
    private String originalFileName;
    private Long totalFileSize;
    private Integer totalChunks;
    private TopicContentType contentType;
    private String mimeType;
    private Set<Integer> uploadedChunks;
    private Long createdAt;
}
