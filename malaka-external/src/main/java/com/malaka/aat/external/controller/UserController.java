package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external")
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/students/type/{type}")
    public BaseResponse getStudents(@PathVariable Long type) {
        return userService.getStudentsByType(type);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/students/course/{courseId}/type/{type}")
    public BaseResponse getStudentsByTypeAndCourseId(
            @PathVariable String courseId, @PathVariable Long type) {
        return userService.getStudentsByCourseIdAndType(courseId, type);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/students/list/type/{typeId}")
    public ResponseWithPagination getStudentsByTypeId(
            @PathVariable Integer typeId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", required = false) String search
    ) {
        return userService.getStudentsWithPagination(page, size, typeId, search);
    }

}
