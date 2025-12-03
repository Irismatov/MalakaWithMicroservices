package com.malaka.aat.internal.service;

import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.enumerators.topic.TopicContentType;
import com.malaka.aat.internal.model.File;
import com.malaka.aat.internal.repository.FileRepository;
import com.malaka.aat.internal.util.FileValidationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling large video/audio file uploads with streaming support.
 * Supports chunked uploads for better handling of large files.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoUploadService {

    @Value("${app.file.video-path:D:/MalakaFiles/videos/}")
    private String videoFilePath;

    @Value("${app.file.chunk-size:5242880}") // 5MB default chunk size
    private long chunkSize;

    private final FileRepository fileRepository;

    // Supported video formats
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".webm"
    );

    // Supported audio formats
    private static final List<String> AUDIO_EXTENSIONS = Arrays.asList(
            ".mp3", ".wav", ".aac", ".flac", ".ogg", ".m4a"
    );


    public File uploadMediaFile(MultipartFile multipartFile, TopicContentType contentType) throws IOException {

        validateMediaFile(multipartFile, contentType);

        // Calculate hash while reading the file (streaming approach)
        String hash = HashUtil.sha256(multipartFile.getBytes());

        // Check if file already exists
        Optional<File> existingFile = fileRepository.findByHash(hash);
        if (existingFile.isPresent()) {
            log.info("Media file already exists with hash: {}", hash);
            File oldFile = existingFile.get();
            File file = new  File();
            file.setHash(hash);
            file.setContentType(oldFile.getContentType());
            file.setFileSize(oldFile.getFileSize());
            file.setOriginalName(oldFile.getOriginalName());
            file.setPath(oldFile.getPath());
            file.setExtension(oldFile.getExtension());
            return fileRepository.save(file);
        }

        // Create date-based folder structure
        LocalDate today = LocalDate.now();
        String folderPath = String.format("%s/%d/%02d/%02d",
                videoFilePath, today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        // Get file extension
        String originalName = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        String fullPath = folderPath + "/" + hash + extension;
        Path filePath = Paths.get(fullPath);

        // Stream the file to disk to avoid loading entire file in memory
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath);
        }

        log.info("Media file uploaded successfully: {}", fullPath);

        // Save metadata to database
        File newFile = new File();
        newFile.setHash(hash);
        newFile.setPath(fullPath);
        newFile.setExtension(extension);
        newFile.setOriginalName(originalName);
        newFile.setFileSize(multipartFile.getSize());
        newFile.setContentType(multipartFile.getContentType());

        return fileRepository.save(newFile);
    }

    /**
     * Upload file chunk (for chunked upload implementation)
     */
    public void uploadChunk(String fileId, MultipartFile chunk, int chunkNumber, int totalChunks) throws IOException {
        String tempPath = videoFilePath + "/temp/" + fileId;
        Path tempFolder = Paths.get(tempPath);

        if (!Files.exists(tempFolder)) {
            Files.createDirectories(tempFolder);
        }

        // Save chunk with proper file handling
        Path chunkPath = Paths.get(tempPath + "/chunk_" + chunkNumber);

        // If chunk already exists, delete it first (for retry scenarios)
        if (Files.exists(chunkPath)) {
            Files.delete(chunkPath);
            log.info("Existing chunk replaced: {}", chunkNumber);
        }

        try (InputStream inputStream = chunk.getInputStream();
             OutputStream outputStream = Files.newOutputStream(chunkPath, StandardOpenOption.CREATE)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        log.info("Chunk {}/{} uploaded for file: {} (size: {} bytes)",
                chunkNumber, totalChunks, fileId, chunk.getSize());
    }

    /**
     * Merge all chunks into final file
     */
    public File mergeChunks(String fileId, String originalFileName, int totalChunks, String contentType) throws IOException {
        String tempPath = videoFilePath + "/temp/" + fileId;
        Path tempFolder = Paths.get(tempPath);

        // Verify temp folder exists
        if (!Files.exists(tempFolder)) {
            throw new IOException("Temp folder not found for upload: " + fileId);
        }

        // Verify all chunks exist
        for (int i = 0; i < totalChunks; i++) {
            Path chunkPath = Paths.get(tempPath + "/chunk_" + i);
            if (!Files.exists(chunkPath)) {
                throw new IOException("Missing chunk: " + i + " for upload: " + fileId);
            }
        }

        // Create final file path
        LocalDate today = LocalDate.now();
        String folderPath = String.format("%s/%d/%02d/%02d",
                videoFilePath, today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        }

        String finalPath = folderPath + "/" + fileId + extension;
        Path finalFile = Paths.get(finalPath);

        // Merge chunks with buffered I/O for better performance
        long totalBytesWritten = 0;
        try (OutputStream outputStream = Files.newOutputStream(finalFile, StandardOpenOption.CREATE)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = Paths.get(tempPath + "/chunk_" + i);
                try (InputStream inputStream = Files.newInputStream(chunkPath)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesWritten += bytesRead;
                    }
                }
                log.debug("Merged chunk {}/{}", i + 1, totalChunks);
            }
        }

        // Calculate hash of final file
        String hash = HashUtil.sha256(Files.readAllBytes(finalFile));

        // Check for duplicate file
        Optional<File> existingFile = fileRepository.findByHash(hash);
        if (existingFile.isPresent()) {
            // Delete newly created file since it's a duplicate
            Files.delete(finalFile);
            // Clean up temp chunks
            deleteDirectory(tempFolder.toFile());
            log.info("Merged file is duplicate, using existing file: {}", hash);
            return existingFile.get();
        }

        // Clean up temp chunks
        deleteDirectory(tempFolder.toFile());

        // Save to database
        File newFile = new File();
        newFile.setHash(hash);
        newFile.setPath(finalPath);
        newFile.setExtension(extension);
        newFile.setOriginalName(originalFileName);
        newFile.setContentType(contentType);
        newFile.setFileSize(totalBytesWritten);

        log.info("Chunks merged successfully: {} (size: {} bytes, hash: {})",
                finalPath, totalBytesWritten, hash);

        return fileRepository.save(newFile);
    }

    private void validateMediaFile(MultipartFile file, TopicContentType contentType) {
        // Use centralized FileValidationUtil for consistency
        switch (contentType) {
            case VIDEO -> FileValidationUtil.validateVideoFile(file);
            case AUDIO -> FileValidationUtil.validateAudioFile(file);
            default -> throw new BadRequestException("Unsupported content type: " + contentType);
        }
    }

    private void deleteDirectory(java.io.File directory) {
        if (directory.exists()) {
            java.io.File[] files = directory.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
