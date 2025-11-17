package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.course.*;
import com.malaka.aat.internal.service.CourseService;

import java.util.List;


@Tag(name = "Kurslar boshqaruvi", description = "Ta'lim kurslarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Yangi kurs yaratish",
            description = "Metodist yangi kurs yaratadi. Kurs nomi, modul soni, tavsif va rasm yuklash talab qilinadi. Kurs holati 001 (CREATED) bo'ladi")
    @PostMapping(value = "/course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse create(@ModelAttribute @Validated CourseCreateDto dto) {
        return courseService.save(dto);
    }

    @Operation(summary = "Kurs rasmini olish",
            description = "Kursga yuklangan rasm faylini olish")
    @GetMapping("/course/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable String id)  {
        Resource resource = courseService.getImage(id);
        return ResponseEntity.ok().body(resource);
    }

    @Operation(summary = "Kurs holatini yangilash",
            description = "Kurs holatini o'zgartirish (001->002->003->006 yoki rad etish). Holat o'zgarishi qoidalarga bog'liq")
    @PutMapping("/course/{id}/state")
    public BaseResponse updateState(@PathVariable String id, @RequestBody @Validated CourseStateUpdateDto dto) {
        return courseService.updateCourseState(id, dto);
    }


    @Operation(summary = "Kurs ma'lumotlarini yangilash",
            description = "Mavjud kursning ma'lumotlarini yangilash. Kurs nomi, tavsif, rasm yangilanishi mumkin")
    @PutMapping("/course/{id}")
    public BaseResponse update(@PathVariable String id, @ModelAttribute @Validated CourseUpdateDto dto) {
        return courseService.update(id, dto);
    }

    @Operation(summary = "Kursga bitta modul qo'shish",
            description = "Kursga yangi modul qo'shish. Modul nomi, mavzu soni, o'qituvchi, fakultet va kafedra kiritiladi")
    @PostMapping("/course/{id}/module")
    public BaseResponse createSingleModule(@PathVariable String id, @RequestBody @Validated CourseAddSingleModuleDto dto) {
        return courseService.addSingleModuleToCourse(id, dto);
    }

    @Operation(summary = "Kurslar ro'yxatini olish",
            description = "Sahifalash va filtrlash bilan kurslar ro'yxatini olish. ADMIN, SUPER_ADMIN, METHODIST va TEACHER ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'METHODIST', 'TEACHER', 'FACULTY_HEAD')")
    @GetMapping("/course")
    public ResponseWithPagination getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ParameterObject @Valid CourseFilterDto filterDto
    ) {
        return courseService.getCourses(page, size, filterDto);
    }

    @GetMapping("/courses")
    public BaseResponse getCourses() {
      return courseService.getCoursesWithoutPagination();
    }

    @Operation(summary = "Kursni ID bo'yicha olish",
            description = "Kursning barcha ma'lumotlari, modullar va mavzular bilan birga olish")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'METHODIST', 'TEACHER', 'FACULTY_HEAD', 'DEPARTMENT_HEAD')")
    @GetMapping("/course/{courseId}")
    public BaseResponse getCourseById(@PathVariable String courseId) {
        return courseService.getCourseById(courseId);
    }

    @Operation(summary = "Kursni o'chirish",
            description = "Kursni soft delete qilish. Faqat ADMIN, SUPER_ADMIN va METHODIST ruxsat etilgan")
    @DeleteMapping("/course/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'METHODIST')")
    public BaseResponse delete(@PathVariable String id) {
        return courseService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/course/students")
    public BaseResponse getStudents(@RequestParam List<String> ids) {
        return courseService.getCoursesForStudents(ids);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/course/topic/{topicId}")
    public BaseResponse findByTopicId(@PathVariable String topicId) {
       return courseService.getCourseByTopicId(topicId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/course/name/{id}")
    public BaseResponse getCourseNameById(@PathVariable String id) {
        return courseService.getCourseNameById(id);
    }

}
