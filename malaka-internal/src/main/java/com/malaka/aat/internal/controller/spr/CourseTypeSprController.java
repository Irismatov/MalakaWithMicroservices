package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprCreateDto;
import com.malaka.aat.internal.dto.spr.coursetype.CourseTypeSprUpdateDto;
import com.malaka.aat.internal.service.spr.CourseTypeSprService;

@Tag(name = "Kurs turlari ma'lumotnomalari", description = "Kurs turlarini boshqarish uchun API'lar (ASOSIY, QO'SHIMCHA)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr/course-type")
public class CourseTypeSprController {

    private final CourseTypeSprService courseTypeSprService;

    @Operation(summary = "Yangi kurs turi yaratish",
            description = "Yangi kurs turi ma'lumotnomasi yaratish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public BaseResponse save(@RequestBody @Validated CourseTypeSprCreateDto dto) {
        return courseTypeSprService.save(dto);
    }

    @Operation(summary = "Kurs turlari ro'yxatini olish",
            description = "Sahifalash bilan kurs turlari ro'yxatini olish")
    @GetMapping
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return courseTypeSprService.getList(page, size);
    }

    @Operation(summary = "Kurs turini ID bo'yicha olish",
            description = "Kurs turi ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        return courseTypeSprService.getById(id);
    }

    @Operation(summary = "Kurs turi ma'lumotlarini yangilash",
            description = "Mavjud kurs turining ma'lumotlarini yangilash. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable Long id,
            @RequestBody @Validated CourseTypeSprUpdateDto dto
    ) {
        return courseTypeSprService.update(id, dto);
    }

    @Operation(summary = "Kurs turini o'chirish",
            description = "Kurs turini soft delete qilish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable Long id) {
        return courseTypeSprService.delete(id);
    }

    @Operation(summary = "Barcha kurs turlarini ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha kurs turlarini sahifalashsiz olish")
    @GetMapping("/list")
    public BaseResponse getAllForList() {
        return courseTypeSprService.getAllForList();
    }
}
