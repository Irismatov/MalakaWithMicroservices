package com.malaka.aat.external.controller;


import com.malaka.aat.core.dto.ResponseWithPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/external")
@RestController
public class CourseController {

    @GetMapping("/course")
    public ResponseWithPagination getCoursesWithPagination(

    ) {

    }


}
