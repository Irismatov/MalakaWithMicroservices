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

    @PostMapping("/enrollment/group/{groupId}/module/{moduleId}/topic/{topicId}/content/{contentId}/startTask")
    public BaseResponse startTask(
            @PathVariable String groupId,
            @PathVariable String moduleId,
            @PathVariable String topicId,
            @PathVariable String contentId
    ) {
        return studentEnrollmentService.startTask(groupId, moduleId, topicId, contentId);
    }

    @PostMapping("/enrollment/group/{groupId}/module/{moduleId}/topic/{topicId}/content/{contentId}/finishTask")
    public BaseResponse finishTask(
            @PathVariable String groupId,
            @PathVariable String moduleId,
            @PathVariable String topicId,
            @PathVariable String contentId
    ) {
        return studentEnrollmentService.finishTask(groupId, moduleId, topicId, contentId);
    }

}
