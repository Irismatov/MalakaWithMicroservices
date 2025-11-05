package com.malaka.aat.internal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.malaka.aat.internal.model.File;
import com.malaka.aat.internal.service.FileService;

@Tag(name = "Fayllar boshqaruvi", description = "Fayllarni yuklash va olish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Faylni ID bo'yicha olish",
            description = "Faylni ID orqali olish. Fayl to'g'ri content type va nomlash bilan qaytariladi")
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable String id) {

        File file = fileService.getFileById(id);

        // Load file resource
        Resource resource = fileService.loadFileAsResource(id);

        // Determine content type - try multiple approaches for reliability
        MediaType mediaType;

        // 1. Try to get content type from database
        String storedContentType = file.getContentType();
        if (storedContentType != null && !storedContentType.isEmpty()) {
            try {
                mediaType = MediaType.parseMediaType(storedContentType);
            } catch (Exception e) {
                // If parsing fails, try other methods
                mediaType = determineMediaTypeFromResource(resource, file);
            }
        } else {
            // 2. Determine from file extension or resource
            mediaType = determineMediaTypeFromResource(resource, file);
        }

        // Return file with proper headers
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getOriginalName() + "\"")
                .body(resource);
    }

    /**
     * Determine media type from file extension/resource
     */
    private MediaType determineMediaTypeFromResource(Resource resource, File file) {
        // Try using Spring's MediaTypeFactory with resource
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(null);

        if (mediaType != null) {
            return mediaType;
        }

        // Fall back to extension-based detection
        String extension = file.getExtension();
        if (extension != null && !extension.isEmpty()) {
            extension = extension.toLowerCase();
            return switch (extension) {
                case ".png" -> MediaType.IMAGE_PNG;
                case ".jpg", ".jpeg" -> MediaType.IMAGE_JPEG;
                case ".gif" -> MediaType.IMAGE_GIF;
                case ".svg" -> MediaType.parseMediaType("image/svg+xml");
                case ".webp" -> MediaType.parseMediaType("image/webp");
                case ".pdf" -> MediaType.APPLICATION_PDF;
                case ".mp4" -> MediaType.parseMediaType("video/mp4");
                case ".mp3" -> MediaType.parseMediaType("audio/mpeg");
                case ".wav" -> MediaType.parseMediaType("audio/wav");
                default -> MediaType.APPLICATION_OCTET_STREAM;
            };
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }


}
