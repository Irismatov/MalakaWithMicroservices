package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.dto.group.GroupCreateDto;
import com.malaka.aat.external.dto.group.GroupUpdateDto;
import com.malaka.aat.external.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external")
public class GroupController {

    private final GroupService groupService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/group")
    public BaseResponse createGroup(@RequestBody @Validated GroupCreateDto dto) {
        return groupService.save(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/group")
    public ResponseWithPagination getGroup(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return groupService.getCoursesWithPagination(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/group/{id}")
    public BaseResponse updateGroup(@PathVariable String id, @RequestBody GroupUpdateDto dto) {
        return groupService.updateGroup(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/group/{id}")
    public BaseResponse deleteGroup(@PathVariable String id) {
        return groupService.deleteGroup(id);
    }

}
