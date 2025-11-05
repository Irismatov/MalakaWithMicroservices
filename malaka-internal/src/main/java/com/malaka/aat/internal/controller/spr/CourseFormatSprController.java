package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprCreateDto;
import com.malaka.aat.internal.dto.spr.courseformat.CourseFormatSprUpdateDto;
import com.malaka.aat.internal.service.spr.CourseFormatSprService;

@Tag(name = "Kurs formatlari ma'lumotnomalari", description = "Kurs formatlarini boshqarish uchun API'lar (OFFLINE, ONLINE, OFFLINE_ONLINE)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr/course-format")
public class CourseFormatSprController {

    private final CourseFormatSprService courseFormatSprService;

    @Operation(summary = "Yangi kurs formati yaratish",
            description = "Yangi kurs formati ma'lumotnomasi yaratish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public BaseResponse save(@RequestBody @Validated CourseFormatSprCreateDto dto) {
        return courseFormatSprService.save(dto);
    }

    @Operation(summary = "Kurs formatlari ro'yxatini olish",
            description = "Sahifalash bilan kurs formatlari ro'yxatini olish")
    @GetMapping
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return courseFormatSprService.getList(page, size);
    }

    @Operation(summary = "Kurs formatini ID bo'yicha olish",
            description = "Kurs formati ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        return courseFormatSprService.getById(id);
    }

    @Operation(summary = "Kurs formati ma'lumotlarini yangilash",
            description = "Mavjud kurs formatining ma'lumotlarini yangilash. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable Long id,
            @RequestBody @Validated CourseFormatSprUpdateDto dto
    ) {
        return courseFormatSprService.update(id, dto);
    }

    @Operation(summary = "Kurs formatini o'chirish",
            description = "Kurs formatini soft delete qilish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable Long id) {
        return courseFormatSprService.delete(id);
    }

    @Operation(summary = "Barcha kurs formatlarini ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha kurs formatlarini sahifalashsiz olish")
    @GetMapping("/list")
    public BaseResponse getAllForList() {
        return courseFormatSprService.getAllForList();
    }
}
