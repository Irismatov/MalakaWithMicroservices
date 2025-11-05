package com.malaka.aat.external.clients;

import com.malaka.aat.core.dto.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
