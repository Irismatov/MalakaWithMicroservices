package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
