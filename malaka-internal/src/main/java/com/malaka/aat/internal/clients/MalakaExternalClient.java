package com.malaka.aat.internal.clients;

import com.malaka.aat.core.dao.CourseLastGroupOrder;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.internal.dto.group.GroupCreateDto;
import com.malaka.aat.internal.dto.group.GroupUpdateDto;
import com.malaka.aat.internal.dto.student_application.StudentApplicationUpdateDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign Client for communicating with malaka-external service.
 * Used to fetch student applications from the external service.
 * Uses service account authentication via MalakaExternalClientConfig.
 */
@FeignClient(
        name = "malaka-external",
        configuration = MalakaExternalClientConfig.class
)
public interface MalakaExternalClient {


    @GetMapping("/api/external/application")
    ResponseWithPagination getApplications(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status
    );

    @PutMapping("/api/external/application/status/{id}")
    ResponseWithPagination updateStatus(@PathVariable String id,
                                        @RequestBody @Validated StudentApplicationUpdateDto dto);

    @GetMapping("/api/external/user/students/type/{type}")
    BaseResponse getStudentsByType(@PathVariable Long type);


    @GetMapping("/api/external/user/students/course/{courseId}/type/{type}")
    BaseResponse getStudentsByCourseIdAndType(@PathVariable String courseId, @PathVariable Long type);

    @PostMapping("/api/external/group")
    BaseResponse createGroup(@RequestBody GroupCreateDto dto);

    @Headers("X-Internal-Request: account-service")
    @GetMapping("/api/external/group")
    ResponseWithPagination getGroupsWithPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/api/external/application/{id}/file")
    ResponseEntity<Resource> getApplicationFile(@PathVariable String id);

    @PutMapping("/api/external/group/{id}")
    BaseResponse updateGroup(@PathVariable String id, @RequestBody GroupUpdateDto dto);

    @DeleteMapping("/api/external/group/{id}")
    BaseResponse deleteGroup(@PathVariable String id);

    @GetMapping("/api/external/group/course/lastGroupOrders")
    List<CourseLastGroupOrder> getCourseLastGroupOrders(@RequestParam List<String> courseIds);
}
