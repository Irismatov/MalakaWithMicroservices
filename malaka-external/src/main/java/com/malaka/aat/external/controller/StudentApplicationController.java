package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.dto.student_application.StudentApplicationCorporateCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationIndividualCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationUpdateDto;
import com.malaka.aat.external.service.StudentApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Student Application", description = "Student application submission endpoints for external users")
@RequestMapping("/api/external")
@RequiredArgsConstructor
@RestController
public class StudentApplicationController {

    private final StudentApplicationService studentApplicationService;

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
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return studentApplicationService.getApplicationsWithPagination(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/application/status/{id}")
    public ResponseWithPagination updateApplicationStatus(@PathVariable String id,
                                                          @RequestBody @Valid StudentApplicationUpdateDto dto) {
        return studentApplicationService.updateApplicationStatus(id, dto);
    }
}
