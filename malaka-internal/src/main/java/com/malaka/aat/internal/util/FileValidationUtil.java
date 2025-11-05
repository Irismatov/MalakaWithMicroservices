package com.malaka.aat.internal.util;

import com.malaka.aat.core.exception.custom.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for validating file types based on business requirements
 */
public class FileValidationUtil {

    // Image file extensions for Course
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg"
    );

    // Video file extensions for Topic content
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".webm", ".mpeg", ".mpg", ".3gp"
    );

    // Audio file extensions for Topic content
    private static final List<String> AUDIO_EXTENSIONS = Arrays.asList(
            ".mp3", ".wav", ".aac", ".flac", ".ogg", ".wma", ".m4a", ".opus"
    );

    // PDF file extension for Topic lecture and presentation
    private static final List<String> PDF_EXTENSIONS = Arrays.asList(".pdf");

    // Word document extensions for Topic test file
    private static final List<String> WORD_EXTENSIONS = Arrays.asList(
            ".doc", ".docx"
    );

    // Image MIME types
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
            "image/bmp", "image/svg+xml"
    );

    // Video MIME types
    private static final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/quicktime", "video/x-msvideo",
            "video/x-ms-wmv", "video/x-flv", "video/x-matroska", "video/webm",
            "video/mpeg", "video/3gpp"
    );

    // Audio MIME types
    private static final List<String> AUDIO_MIME_TYPES = Arrays.asList(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav", "audio/aac",
            "audio/flac", "audio/ogg", "audio/x-ms-wma", "audio/mp4", "audio/opus"
    );

    // PDF MIME types
    private static final List<String> PDF_MIME_TYPES = Arrays.asList(
            "application/pdf"
    );

    // Word MIME types
    private static final List<String> WORD_MIME_TYPES = Arrays.asList(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * Validates that the uploaded file is an image (for Course)
     * @param file the multipart file to validate
     * @throws BadRequestException if the file is not an image
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("File must have a valid name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        String contentType = file.getContentType();

        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file type. Course image must be one of: " + IMAGE_EXTENSIONS +
                    ". Received: " + extension
            );
        }

        if (contentType != null && !IMAGE_MIME_TYPES.stream()
                .anyMatch(mime -> contentType.toLowerCase().startsWith(mime))) {
            throw new BadRequestException(
                    "Invalid file content type. Expected image file but received: " + contentType
            );
        }
    }

    /**
     * Validates that the uploaded file is a video (for Topic content)
     * @param file the multipart file to validate
     * @throws BadRequestException if the file is not a video
     */
    public static void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("File must have a valid name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        String contentType = file.getContentType();

        if (!VIDEO_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file type. Video file must be one of: " + VIDEO_EXTENSIONS +
                    ". Received: " + extension
            );
        }

        if (contentType != null && !VIDEO_MIME_TYPES.stream()
                .anyMatch(mime -> contentType.toLowerCase().startsWith(mime))) {
            throw new BadRequestException(
                    "Invalid file content type. Expected video file but received: " + contentType
            );
        }
    }

    /**
     * Validates that the uploaded file is audio (for Topic content)
     * @param file the multipart file to validate
     * @throws BadRequestException if the file is not audio
     */
    public static void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("File must have a valid name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        String contentType = file.getContentType();

        if (!AUDIO_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file type. Audio file must be one of: " + AUDIO_EXTENSIONS +
                    ". Received: " + extension
            );
        }

        if (contentType != null && !AUDIO_MIME_TYPES.stream()
                .anyMatch(mime -> contentType.toLowerCase().startsWith(mime))) {
            throw new BadRequestException(
                    "Invalid file content type. Expected audio file but received: " + contentType
            );
        }
    }

    /**
     * Validates that the uploaded file is a PDF (for Topic lecture and presentation)
     * @param file the multipart file to validate
     * @throws BadRequestException if the file is not a PDF
     */
    public static void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("File must have a valid name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        String contentType = file.getContentType();

        if (!PDF_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file type. Lecture file must be PDF. Received: " + extension
            );
        }

        if (contentType != null && !PDF_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    "Invalid file content type. Expected PDF but received: " + contentType
            );
        }
    }

    /**
     * Validates that the uploaded file is a Word document (for Topic test file)
     * @param file the multipart file to validate
     * @throws BadRequestException if the file is not a Word document
     */
    public static void validateWordFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("File must have a valid name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        String contentType = file.getContentType();

        if (!WORD_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file type. Test file must be Word document (.doc or .docx). Received: " + extension
            );
        }

        if (contentType != null && !WORD_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException(
                    "Invalid file content type. Expected Word document but received: " + contentType
            );
        }
    }

    /**
     * Extracts file extension from filename
     * @param filename the filename
     * @return the file extension including the dot (e.g., ".pdf")
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
