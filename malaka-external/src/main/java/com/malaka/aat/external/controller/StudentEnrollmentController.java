package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.service.StudentEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;


    @GetMapping("/enrollment/course/{courseId}")
    public BaseResponse getEnrollmentByCourseId(@PathVariable String courseId) {
        return studentEnrollmentService.findEnrollmentByCourseId(courseId);
    }


}
