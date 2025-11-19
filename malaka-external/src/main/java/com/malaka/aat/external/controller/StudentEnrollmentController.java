package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.service.StudentEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    @PostMapping("/enrollment/group/{groupId}")
    public BaseResponse updateOrCreateStudentEnrollment(@PathVariable String groupId) {
        return studentEnrollmentService.updateOrCreateStudentEnrollment(groupId);
    }

    @PostMapping("/enrollment/group/{groupId}/startCourse")
    public BaseResponse startCourse(@PathVariable String groupId) {
        return studentEnrollmentService.startCourse(groupId);
    }

}
