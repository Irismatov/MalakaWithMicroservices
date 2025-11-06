package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.external.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/course")
    public ResponseWithPagination getCoursesWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return courseService.getCoursesWithPagination(page, size);
    }


}
