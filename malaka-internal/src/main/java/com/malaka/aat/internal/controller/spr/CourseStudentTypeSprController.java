package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprCreateDto;
import com.malaka.aat.internal.dto.spr.coursestudenttype.CourseStudentTypeSprUpdateDto;
import com.malaka.aat.internal.service.spr.CourseStudentTypeSprService;

@Tag(name = "Kurs talaba turlari ma'lumotnomalari", description = "Kurs talaba turlarini boshqarish uchun API'lar (XODIM, TIF ISHTIROKCHISI)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr/course-student-type")
public class CourseStudentTypeSprController {

    private final CourseStudentTypeSprService courseStudentTypeSprService;

    @Operation(summary = "Yangi kurs talaba turi yaratish",
            description = "Yangi kurs talaba turi ma'lumotnomasi yaratish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public BaseResponse save(@RequestBody @Validated CourseStudentTypeSprCreateDto dto) {
        return courseStudentTypeSprService.save(dto);
    }

    @Operation(summary = "Kurs talaba turlari ro'yxatini olish",
            description = "Sahifalash bilan kurs talaba turlari ro'yxatini olish")
    @GetMapping
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return courseStudentTypeSprService.getList(page, size);
    }

    @Operation(summary = "Kurs talaba turini ID bo'yicha olish",
            description = "Kurs talaba turi ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        return courseStudentTypeSprService.getById(id);
    }

    @Operation(summary = "Kurs talaba turi ma'lumotlarini yangilash",
            description = "Mavjud kurs talaba turining ma'lumotlarini yangilash. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable Long id,
            @RequestBody @Validated CourseStudentTypeSprUpdateDto dto
    ) {
        return courseStudentTypeSprService.update(id, dto);
    }

    @Operation(summary = "Kurs talaba turini o'chirish",
            description = "Kurs talaba turini soft delete qilish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable Long id) {
        return courseStudentTypeSprService.delete(id);
    }

    @Operation(summary = "Barcha kurs talaba turlarini ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha kurs talaba turlarini sahifalashsiz olish")
    @GetMapping("/list")
    public BaseResponse getAllForList() {
        return courseStudentTypeSprService.getAllForList();
    }
}
