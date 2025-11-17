package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.dto.student_application.StudentApplicationCorporateCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationIndividualCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationUpdateDto;
import com.malaka.aat.external.model.File;
import com.malaka.aat.external.service.StudentApplicationService;
import com.malaka.aat.external.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Student Application", description = "Student application submission endpoints for external users")
@RequestMapping("/api/external")
@RequiredArgsConstructor
@RestController
public class StudentApplicationController {

    private final StudentApplicationService studentApplicationService;
    private final FileService fileService;

    @Operation(summary = "Submit individual student application",
            description = "Allows individual students to submit course applications with required documents")
    @PostMapping("/application/individual")
    public BaseResponse saveIndividualApplication(@ModelAttribute @Valid StudentApplicationIndividualCreateDto dto) {
        return studentApplicationService.saveIndividualApplication(dto);
    }

    @Operation(summary = "Submit corporate student application",
            description = "Allows corporate entities to submit course applications for multiple employees")
    @PostMapping("/application/corporate")
    public BaseResponse saveCorporateApplication(@ModelAttribute @Valid StudentApplicationCorporateCreateDto dto) {
        return studentApplicationService.saveCorporateApplication(dto);
    }

    @Operation(summary = "Get all student applications with pagination",
            description = "Retrieves all student applications (both individual and corporate) with pagination support")
    @GetMapping("/application")
    public ResponseWithPagination getApplications(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader(value = "X-Internal-Request",required = false) String serviceName
    ) {
        boolean isInternal = serviceName != null;
        return studentApplicationService.getApplicationsWithPagination(page, size, isInternal);
    }

    @GetMapping("/application/{id}/file")
    public ResponseEntity<Resource> getApplicationFile(@PathVariable  String id) {
        File file = studentApplicationService.getApplicationFile(id);

        Resource resource = fileService.loadFileAsResource(file.getId());
        MediaType mediaType;

        String storedContentType = file.getContentType();
        if (storedContentType != null && !storedContentType.isEmpty()) {
            try {
                mediaType = MediaType.parseMediaType(storedContentType);
            } catch (Exception e) {
                mediaType = determineMediaType(resource, file);
            }
        } else {
            mediaType = determineMediaType(resource, file);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getOriginalName()
                        + "\"")
                .contentType(mediaType)
                .body(resource);

    }

    private MediaType determineMediaType(Resource resource, File file) {
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(null);

        if (mediaType != null) {
            return mediaType;
        }

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

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/application/status/{id}")
    public ResponseWithPagination updateApplicationStatus(
            @PathVariable String id,
            @RequestBody @Valid StudentApplicationUpdateDto dto,
            @RequestHeader(value = "X-Internal-Request", required = false) String serviceName
    ) {
        boolean isInternal = serviceName != null;
        return studentApplicationService.updateApplicationStatus(id, dto, isInternal);
    }
}
