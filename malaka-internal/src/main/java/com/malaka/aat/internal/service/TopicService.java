package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.AuthException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.internal.dto.module.ModuleDto;
import com.malaka.aat.internal.dto.test.TestDto;
import com.malaka.aat.internal.dto.topic.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.raw.Mod;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.enumerators.topic.TopicContentType;
import com.malaka.aat.internal.model.*;
import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.repository.ModuleRepository;
import com.malaka.aat.internal.repository.TopicRepository;
import com.malaka.aat.internal.repository.UserRepository;
import com.malaka.aat.internal.util.FileValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final VideoUploadService videoUploadService;
    private final FileService fileService;
    private final SessionService sessionService;
    private final com.malaka.aat.internal.repository.TestRepository testRepository;
    private final com.malaka.aat.internal.repository.FileRepository fileRepository;

    // Track chunk upload sessions (uploadId -> session metadata)
    private final Map<String, ChunkUploadSession> chunkUploadSessions = new ConcurrentHashMap<>();

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse uploadContentFile(String topicId, TopicContentUploadDto dto) throws IOException {
        BaseResponse response = new BaseResponse();

        Topic topic = getTopicWithAuthorization(topicId);

        // Validate module state is NEW (001)
        validateModuleStateForUploadOrUpdate(topic.getModule());

        TopicContentType type = TopicContentType.getFromValue(dto.getContentType());

        switch (type) {
            case VIDEO, AUDIO -> {
                File uploadedFile = videoUploadService.uploadMediaFile(dto.getFile(), type);
                topic.setContentFile(uploadedFile);
            }
        }

        topic.setContentType(type);
        Topic savedTopic = topicRepository.save(topic);
        TopicDto topicDto = new TopicDto(savedTopic);
        response.setData(topicDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        log.info("Content file uploaded for topic: {}", topicId);

        return response;
    }

    /**
     * Initialize chunk upload session
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse initChunkUpload(String topicId, InitChunkUploadDto dto) {
        BaseResponse response = new BaseResponse();

        Topic topic = getTopicWithAuthorization(topicId);

        // Validate module state is NEW (001)
        validateModuleStateForUploadOrUpdate(topic.getModule());

        // Validate content type
        TopicContentType contentType = TopicContentType.getFromValue(dto.getContentType());
        if (contentType != TopicContentType.VIDEO && contentType != TopicContentType.AUDIO) {
            throw new BadRequestException("Chunk upload only supported for video and audio content");
        }

        // Generate unique upload ID
        String uploadId = UUID.randomUUID().toString();

        // Create upload session
        ChunkUploadSession session = new ChunkUploadSession();
        session.setUploadId(uploadId);
        session.setTopicId(topicId);
        session.setOriginalFileName(dto.getOriginalFileName());
        session.setTotalFileSize(dto.getTotalFileSize());
        session.setTotalChunks(dto.getTotalChunks());
        session.setContentType(contentType);
        session.setMimeType(dto.getMimeType());
        session.setUploadedChunks(new HashSet<>());
        session.setCreatedAt(System.currentTimeMillis());

        chunkUploadSessions.put(uploadId, session);

        log.info("Chunk upload initialized: uploadId={}, topicId={}, totalChunks={}",
                uploadId, topicId, dto.getTotalChunks());


        ChunkUploadResponseDto chunkUploadSessionInitialized = new ChunkUploadResponseDto(
                uploadId,
                "Chunk upload session initialized",
                dto.getTotalChunks(),
                0
        );
        response.setData(chunkUploadSessionInitialized);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    /**
     * Upload individual chunk
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse uploadChunk(String topicId, String uploadId, Integer chunkNumber,
                                               MultipartFile chunk) throws IOException {
        BaseResponse response = new BaseResponse();

        // Verify session exists
        ChunkUploadSession session = chunkUploadSessions.get(uploadId);
        if (session == null) {
            throw new NotFoundException("Upload session not found: " + uploadId);
        }

        // Verify topic matches
        if (!session.getTopicId().equals(topicId)) {
            throw new BadRequestException("Upload session does not match topic");
        }

        // Verify chunk number is valid
        if (chunkNumber < 0 || chunkNumber >= session.getTotalChunks()) {
            throw new BadRequestException("Invalid chunk number: " + chunkNumber);
        }

        // Upload chunk using VideoUploadService
        videoUploadService.uploadChunk(uploadId, chunk, chunkNumber, session.getTotalChunks());

        // Track uploaded chunk
        session.getUploadedChunks().add(chunkNumber);

        log.info("Chunk uploaded: uploadId={}, chunkNumber={}, progress={}/{}",
                uploadId, chunkNumber, session.getUploadedChunks().size(), session.getTotalChunks());

        ChunkUploadResponseDto chunkUploadedSuccessfully = new ChunkUploadResponseDto(
                uploadId,
                "Chunk uploaded successfully",
                session.getTotalChunks(),
                session.getUploadedChunks().size()
        );
        response.setData(chunkUploadedSuccessfully);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    /**
     * Finalize chunk upload and merge chunks
     */
    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse finalizeChunkUpload(String topicId, String uploadId) throws IOException {
        BaseResponse response = new BaseResponse();

        // Verify session exists
        ChunkUploadSession session = chunkUploadSessions.get(uploadId);
        if (session == null) {
            throw new NotFoundException("Upload session not found: " + uploadId);
        }

        // Verify topic matches
        if (!session.getTopicId().equals(topicId)) {
            throw new BadRequestException("Upload session does not match topic");
        }

        // Verify all chunks uploaded
        if (session.getUploadedChunks().size() != session.getTotalChunks()) {
            throw new BadRequestException("Not all chunks uploaded. Expected: " + session.getTotalChunks()
                    + ", Uploaded: " + session.getUploadedChunks().size());
        }

        Topic topic = getTopicWithAuthorization(topicId);

        // Merge chunks using VideoUploadService
        File uploadedFile = videoUploadService.mergeChunks(
                uploadId,
                session.getOriginalFileName(),
                session.getTotalChunks(),
                session.getMimeType()
        );

        // Update file size
        uploadedFile.setFileSize(session.getTotalFileSize());
        fileRepository.save(uploadedFile);

        // Update topic
        topic.setContentFile(uploadedFile);
        topic.setContentType(session.getContentType());
        Topic savedTopic = topicRepository.save(topic);

        // Clean up session
        chunkUploadSessions.remove(uploadId);

        TopicDto topicDto = new TopicDto(savedTopic);
        response.setData(topicDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        log.info("Chunk upload finalized: uploadId={}, topicId={}", uploadId, topicId);

        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse uploadLectureFile(String topicId, MultipartFile file) {
        BaseResponse response = new BaseResponse();

        Topic topic = getTopicWithAuthorization(topicId);

        // Validate module state is NEW (001)
        validateModuleStateForUploadOrUpdate(topic.getModule());

        // Validate that the file is a PDF
        FileValidationUtil.validatePdfFile(file);

        try {
            File uploadedFile = fileService.save(file);
            topic.setLectureFile(uploadedFile);
            Topic savedTopic = topicRepository.save(topic);

            TopicDto topicDto = new TopicDto(savedTopic);
            response.setData(topicDto);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

            log.info("Lecture file uploaded for topic: {}", topicId);
        } catch (IOException e) {
            log.error("Failed to upload lecture file for topic: {}", topicId, e);
            throw new SystemException();
        }

        return response;
    }


    public BaseResponse getTopicById(String id) {
        BaseResponse response = new BaseResponse();

        // Use findByIdWithTest to eagerly fetch the test and avoid LazyInitializationException
        Topic topic = topicRepository.findByIdWithTest(id)
                .orElseThrow(() -> new NotFoundException("Topic not found with id: " + id));

        TopicDto topicDto = new TopicDto(topic);
        response.setData(topicDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        return response;
    }

    public BaseResponse getTopicsByModule(String moduleId) {
        BaseResponse response = new BaseResponse();

        List<Topic> topics = topicRepository.findByModuleIdOrderByOrderAsc(moduleId);
        List<TopicDto> topicDtos = topics.stream()
                .map(TopicDto::new)
                .collect(Collectors.toList());

        response.setData(topicDtos);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        return response;
    }

    public BaseResponse getTopicsByTeacher(String teacherId) {
        BaseResponse response = new BaseResponse();

        Optional<Module> byTeacherId = moduleRepository.findByTeacherId(teacherId);

        if (byTeacherId.isEmpty()) {
            throw new NotFoundException("Module not found teacher id: " + teacherId);
        }

        List<TopicDto> topicDtos = byTeacherId.get().getTopics().stream()
                .map(TopicDto::new)
                .collect(Collectors.toList());

        response.setData(topicDtos);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        return response;
    }

    private Topic getTopicWithAuthorization(String topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic not found with id: " + topicId));

        // Verify the authenticated user is the teacher assigned to this module
        String currentUserId = sessionService.getCurrentUserId();
        User user = userRepository.findById(currentUserId).get();

        // Admin and Super Admin can create topics for any module
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(auth -> auth.getName().equals("ADMIN") ||
                        auth.getName().equals("SUPER_ADMIN"));

        // Check if user is authorized (teacher of the topic or admin)
        if (!isAdmin && (topic.getModule().getTeacher() == null || !topic.getModule().getTeacher().getId().equals(currentUserId))) {
            throw new AuthException();
        }

        return topic;
    }

    private void validateModuleStateForUploadOrUpdate(Module module) {
        String moduleState = module.getModuleState();
        String courseState = module.getCourse().getState();

        if (courseState.equals("004")) {
            return;
        } else if (courseState.equals("002") && moduleState.equals("001")) {
            return;
        }

        throw new BadRequestException("Module state or course state not valid");
    }

    public BaseResponse uploadPresentationFile(String topicId, MultipartFile file) {
        BaseResponse response = new BaseResponse();

        Topic topic = getTopicWithAuthorization(topicId);

        // Validate module state is NEW (001)
        validateModuleStateForUploadOrUpdate(topic.getModule());

        // Validate that the file is a PDF
        FileValidationUtil.validatePdfFile(file);

        try {
            File uploadedFile = fileService.save(file);
            topic.setPresentationFile(uploadedFile);
            Topic savedTopic = topicRepository.save(topic);

            TopicDto topicDto = new TopicDto(savedTopic);
            response.setData(topicDto);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

            log.info("Presentation file uploaded for topic: {}", topicId);
        } catch (IOException e) {
            log.error("Failed to upload presentation file for topic: {}", topicId, e);
            throw new SystemException();
        }

        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse saveTest(String topicId, com.malaka.aat.internal.dto.test.TestCreateDto testCreateDto) {
        BaseResponse response = new BaseResponse();

        Topic topic = getTopicWithAuthorization(topicId);

        // Validate module state is NEW (001)
        validateModuleStateForUploadOrUpdate(topic.getModule());


        if (topic.getTest() != null) {
            Test test = topic.getTest();
            test.setIsDeleted((short) 1);
            testRepository.save(test);
        }

        // Create test entity
        com.malaka.aat.internal.model.Test test = new com.malaka.aat.internal.model.Test();
        test.setTopic(topic);
        test.setAttemptLimit(testCreateDto.getAttemptLimit());

        List<com.malaka.aat.internal.model.TestQuestion> testQuestions = new ArrayList<>();

        for (com.malaka.aat.internal.dto.test.TestQuestionCreateDto questionDto : testCreateDto.getQuestions()) {
            com.malaka.aat.internal.model.TestQuestion testQuestion = new com.malaka.aat.internal.model.TestQuestion();
            testQuestion.setQuestionText(questionDto.getQuestionText());
            // Defensive null check: default to 0 if hasImage is null
            testQuestion.setHasImage(questionDto.getHasImage() != null ? questionDto.getHasImage() : (short) 0);
            testQuestion.setTest(test);

            // Extract file ID from imgUrl if present
            if (questionDto.getImgUrl() != null && !questionDto.getImgUrl().isEmpty()) {
                String fileId = extractFileIdFromUrl(questionDto.getImgUrl());
                if (fileId != null) {
                    try {
                        File questionImageFile = fileService.getFileById(fileId);
                        testQuestion.setQuestionImage(questionImageFile);
                    } catch (NotFoundException e) {
                        log.warn("Question image file not found with id: {}", fileId);
                    }
                }
            }

            // Process options
            List<com.malaka.aat.internal.model.QuestionOption> questionOptions = new ArrayList<>();

            for (com.malaka.aat.internal.dto.test.TestQuestionOptionCreateDto optionDto : questionDto.getOptions()) {
                com.malaka.aat.internal.model.QuestionOption questionOption = new com.malaka.aat.internal.model.QuestionOption();
                questionOption.setOptionText(optionDto.getOptionText());
                // Defensive null checks: default to 0 if null
                questionOption.setIsCorrect(optionDto.getIsCorrect() != null ? optionDto.getIsCorrect() : (short) 0);
                questionOption.setHasImage(optionDto.getHasImage() != null ? optionDto.getHasImage() : (short) 0);
                questionOption.setQuestion(testQuestion);

                // Extract file ID from imgUrl if present
                if (optionDto.getImgUrl() != null && !optionDto.getImgUrl().isEmpty()) {
                    String fileId = extractFileIdFromUrl(optionDto.getImgUrl());
                    if (fileId != null) {
                        try {
                            File optionImageFile = fileService.getFileById(fileId);
                            questionOption.setImageFile(optionImageFile);
                        } catch (NotFoundException e) {
                            log.warn("Option image file not found with id: {}", fileId);
                        }
                    }
                }

                questionOptions.add(questionOption);
            }

            testQuestion.setOptions(questionOptions);
            testQuestions.add(testQuestion);
        }

        test.setQuestions(testQuestions);
        // Save test with all questions and options (cascade will save questions and options)
        com.malaka.aat.internal.model.Test savedTest = testRepository.save(test);

        savedTest.getTopic().setTest(savedTest);
        TopicDto topicDto = new TopicDto(savedTest.getTopic());
        response.setData(topicDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        log.info("Test saved for topic: {} with {} questions", topicId, savedTest.getQuestions().size());

        return response;
    }

    /**
     * Extract file ID from URL
     * Supports formats:
     * - http://localhost:8585/api/file/{fileId}
     * - /api/file/{fileId}
     * - {fileId} (just the UUID)
     */
    private String extractFileIdFromUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.isEmpty()) {
            return null;
        }

        // Pattern 1: Full URL - http://localhost:8585/api/file/{fileId}
        if (imgUrl.contains("/api/file/")) {
            String[] parts = imgUrl.split("/api/file/");
            if (parts.length == 2) {
                return parts[1].trim();
            }
        }

        // Pattern 2: Just the file ID (UUID format)
        // UUIDs are typically 36 characters with dashes
        if (imgUrl.length() >= 36 && imgUrl.contains("-")) {
            return imgUrl.trim();
        }

        return null;
    }

    public Topic findById(String id) {
        return topicRepository.findById(id).orElseThrow(() -> new NotFoundException(("Topic not found with id: " + id)));
    }

    public Resource getTopicPresentationAsResource(String topicId) {
        Topic topic = findById(topicId);

        File presentationFile = topic.getPresentationFile();

        if (presentationFile == null) {
            throw new NotFoundException("Presentation file not found for a topic with id: " + topicId);
        }

        Path path = Paths.get(presentationFile.getPath());
        Resource resource = new FileSystemResource(path);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new NotFoundException(("Problem occurred finding a video for a topic with id: " + topicId));
        }

    }


    public Resource getTopicLectureFileAsResource(String topicId) {
        Topic topic = findById(topicId);

        File lectureFile = topic.getLectureFile();

        if (lectureFile == null) {
            throw new NotFoundException("Lecture file not found for the topic with id: " + topicId);
        }

        Path path = Paths.get(lectureFile.getPath());
        Resource resource = new FileSystemResource(path);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new NotFoundException(("Problem occurred finding a video for a topic with id: " + topicId));
        }

    }

    public Resource getTopicContentAsResource(String topicId) {
        Topic topic = findById(topicId);

        File contentFile = topic.getContentFile();
        if (contentFile == null) {
            throw new NotFoundException("Not content file found for topic with id: " + topicId);
        }
        Path path = Paths.get(contentFile.getPath());
        Resource resource = new FileSystemResource(path);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new NotFoundException(("Problem occurred finding a video for a topic with id: " + topicId));
        }

    }

    public ResponseEntity<Resource> getTopicContentWithHeaders(String topicId) {
        Topic topic = findById(topicId);

        // Check if content type is ZOOM - no file exists for ZOOM
        if (topic.getContentType() == TopicContentType.ZOOM) {
            throw new NotFoundException("Content file not found for ZOOM type topic with id: " + topicId);
        }

        File contentFile = topic.getContentFile();
        if (contentFile == null) {
            throw new NotFoundException("Content file not found for topic with id: " + topicId);
        }

        Path path = Paths.get(contentFile.getPath());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new NotFoundException("Problem occurred finding content file for topic with id: " + topicId);
        }

        String filename = resource.getFilename();
        if (filename == null) {
            filename = "content";
        }

        // Determine media type - use File's contentType if available, otherwise use Topic's contentType enum
        MediaType mediaType;
        if (contentFile.getContentType() != null && !contentFile.getContentType().isEmpty()) {
            mediaType = MediaType.parseMediaType(contentFile.getContentType());
        } else if (topic.getContentType() == TopicContentType.VIDEO) {
            mediaType = MediaType.parseMediaType("video/*");
        } else if (topic.getContentType() == TopicContentType.AUDIO) {
            mediaType = MediaType.parseMediaType("audio/*");
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }



    public ResponseEntity<Resource> streamTopicContent(String topicId, String rangeHeader) {
        try {
            // Get topic and validate
            Topic topic = findById(topicId);

            if (topic.getContentFile() == null) {
                throw new NotFoundException("Topic content file not found for topic id: " + topicId);
            }

            File contentFile = topic.getContentFile();
            Path videoPath = Paths.get(contentFile.getPath());

            if (!Files.exists(videoPath)) {
                throw new NotFoundException("Video file does not exist at path: " + contentFile.getPath());
            }

            Resource video = new FileSystemResource(videoPath);
            long fileLength = video.contentLength();

            // Determine content type
            MediaType mediaType = MediaTypeFactory.getMediaType(video)
                    .orElse(MediaType.parseMediaType(contentFile.getContentType()));

            // If no Range header, return full file
            if (rangeHeader == null || rangeHeader.isEmpty()) {
                log.info("Streaming full video for topic: {} ({})", topicId, fileLength);
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + contentFile.getOriginalName() + "\"")
                        .contentLength(fileLength)
                        .body(video);
            }

            // Parse Range header (e.g., "bytes=0-1023")
            List<HttpRange> ranges;
            try {
                ranges = HttpRange.parseRanges(rangeHeader);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Range header: {}", rangeHeader);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                        .build();
            }

            if (ranges.isEmpty()) {
                log.info("Streaming full video for topic: {} (no valid ranges)", topicId);
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .contentLength(fileLength)
                        .body(video);
            }

            // Handle first range (browsers typically request one range at a time)
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(fileLength);
            long end = range.getRangeEnd(fileLength);
            long rangeLength = end - start + 1;

            log.info("Streaming partial video for topic: {} (bytes {}-{}/{})", topicId, start, end, fileLength);

            // Create InputStreamResource for the requested range
            java.io.RandomAccessFile randomAccessFile = new java.io.RandomAccessFile(videoPath.toFile(), "r");
            randomAccessFile.seek(start);

            java.io.InputStream inputStream = new java.io.InputStream() {
                long bytesRead = 0;

                @Override
                public int read() throws IOException {
                    if (bytesRead >= rangeLength) {
                        return -1;
                    }
                    bytesRead++;
                    return randomAccessFile.read();
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (bytesRead >= rangeLength) {
                        return -1;
                    }
                    long remainingBytes = rangeLength - bytesRead;
                    int bytesToRead = (int) Math.min(len, remainingBytes);
                    int actualBytesRead = randomAccessFile.read(b, off, bytesToRead);
                    if (actualBytesRead > 0) {
                        bytesRead += actualBytesRead;
                    }
                    return actualBytesRead;
                }

                @Override
                public void close() throws IOException {
                    randomAccessFile.close();
                }
            };

            org.springframework.core.io.InputStreamResource inputStreamResource =
                    new org.springframework.core.io.InputStreamResource(inputStream);

            // Return partial content (206)
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + contentFile.getOriginalName() + "\"")
                    .contentLength(rangeLength)
                    .body(inputStreamResource);

        } catch (IOException e) {
            log.error("Error streaming video for topic: {}", topicId, e);
            throw new SystemException();
        }
    }


    public BaseResponse deleteTopic(String id) {
        BaseResponse response = new BaseResponse();
        Topic deletedTopic = findById(id);

        Module module = deletedTopic.getModule();
        validateModuleStateForUploadOrUpdate(module);
        topicRepository.delete(deletedTopic);
        module = moduleRepository.findById(module.getId()).get();
        List<Topic> topics = module.getTopics().stream().sorted(Comparator.comparing(Topic::getId)).toList();
        int order = deletedTopic.getOrder()-1;
        for (int i = order; i < topics.size(); i++) {
            topics.get(i).setOrder(topics.get(i).getOrder() - 1);
        }

        topicRepository.saveAll(topics);
        module = moduleRepository.findById(module.getId()).get();
        ModuleDto moduleDto = new ModuleDto(module);
        response.setData(moduleDto);
        return response;
    }


    public BaseResponse update(String id, TopicUpdateDto dto) {
        BaseResponse response = new BaseResponse();
        Topic topic = findById(id);
        validateModuleStateForUploadOrUpdate(topic.getModule());

        if (dto.getName() != null) {
            topic.setName(dto.getName());
        }
        Topic savedTopic = topicRepository.save(topic);

        ModuleDto moduleDto = new ModuleDto(savedTopic.getModule());
        response.setData(moduleDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    public BaseResponse getTopicTestById(String id) {
        BaseResponse response = new BaseResponse();
        Topic byId = findById(id);
        Test test = byId.getTest();
        TestDto testDto = new TestDto(test);
        response.setData(testDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
