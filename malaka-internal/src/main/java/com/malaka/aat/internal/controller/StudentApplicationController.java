package com.malaka.aat.internal.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.internal.dto.student_application.StudentApplicationUpdateDto;
import com.malaka.aat.internal.service.StudentApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Student Applications (Internal)", description = "APIs for methodists to view and manage student applications")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class StudentApplicationController {

    private final StudentApplicationService studentApplicationService;

    @Operation(summary = "Get all student applications with pagination",
            description = "Fetches all student applications from external service for review by methodists")
    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/application")
    public ResponseWithPagination getApplicationsWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return studentApplicationService.getApplicationsWithPagination(page, size);
    }

    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/application/{id}/file")
    public ResponseEntity<Resource> getApplicationFile(@PathVariable String id) {
        return studentApplicationService.getApplicationFile(id);
    }

    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/application/status/{id}")
    public ResponseWithPagination updateStatus(@PathVariable String id, @RequestBody @Validated StudentApplicationUpdateDto dto) {
        return studentApplicationService.updateStatus(id, dto);
    }

}
