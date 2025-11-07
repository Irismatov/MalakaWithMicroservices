package com.malaka.aat.external.clients;


import com.malaka.aat.core.dto.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign Client for communicating with malaka-internal service.
 * Uses Eureka service discovery with load balancing.
 */
@FeignClient(
        name = "malaka-internal",
        configuration = MalakaInternalClientConfig.class
)
public interface MalakaInternalClient {

    /**
     * Retrieves course details by ID from malaka-internal service.
     *
     * @param courseId the course ID to retrieve
     * @return BaseResponse containing course data or error information
     */
    @GetMapping("/api/course/{courseId}")
    BaseResponse getCourseById(@PathVariable("courseId") String courseId);

    @GetMapping("/api/course/students")
    BaseResponse getStudentCourses(@RequestParam List<String> ids);

    @GetMapping("/api/course/topic/{topicId}")
    BaseResponse getCourseByTopicId(@PathVariable String topicId);

    @GetMapping("/api/topic/{topicId}/content/stream")
    ResponseEntity<Resource> streamContentFile(@PathVariable String topicId, @RequestHeader(value = "Range", required = false) String rangeHeader);

    @GetMapping("/api/topic/{topicId}/presentation")
    ResponseEntity<Resource> presentationFile(@PathVariable String topicId);

    @GetMapping("/api/topic/{topicId}/lecture")
    ResponseEntity<Resource> lectureFile(@PathVariable String topicId);

    @GetMapping("/api/topic/{topicId}/test")
    BaseResponse getTest(@PathVariable String topicId);

}
