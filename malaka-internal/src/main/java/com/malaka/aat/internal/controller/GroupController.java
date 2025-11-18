package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.internal.dto.group.GroupCreateDto;
import com.malaka.aat.internal.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GroupController {

    private final GroupService groupService;

    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/group")
    public BaseResponse createGroup(@RequestBody GroupCreateDto dto) {
        return groupService.save(dto);
    }

    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/group")
    public ResponseWithPagination getCourses(@RequestParam(value = "page", defaultValue = "0")int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        return groupService.getGroupsWithPagination(page, size);
    }

}
