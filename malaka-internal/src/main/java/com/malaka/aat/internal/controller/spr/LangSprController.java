package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.lang.LangSprCreateDto;
import com.malaka.aat.internal.dto.spr.lang.LangSprUpdateDto;
import com.malaka.aat.internal.service.spr.LangSprService;

@Tag(name = "Tillar ma'lumotnomalari", description = "Til ma'lumotnomalarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr/lang")
public class LangSprController {

    private final LangSprService langSprService;

    @Operation(summary = "Yangi til yaratish",
            description = "Yangi til ma'lumotnomasi yaratish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public BaseResponse save(@RequestBody @Validated LangSprCreateDto dto) {
        return langSprService.save(dto);
    }

    @Operation(summary = "Tillar ro'yxatini olish",
            description = "Sahifalash bilan tillar ro'yxatini olish")
    @GetMapping
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return langSprService.getList(page, size);
    }

    @Operation(summary = "Tilni ID bo'yicha olish",
            description = "Til ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        return langSprService.getById(id);
    }

    @Operation(summary = "Til ma'lumotlarini yangilash",
            description = "Mavjud tilning ma'lumotlarini yangilash. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable Long id,
            @RequestBody @Validated LangSprUpdateDto dto
    ) {
        return langSprService.update(id, dto);
    }

    @Operation(summary = "Tilni o'chirish",
            description = "Tilni soft delete qilish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable Long id) {
        return langSprService.delete(id);
    }

    @Operation(summary = "Barcha tillarni ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha tillarni sahifalashsiz olish")
    @GetMapping("/list")
    public BaseResponse getAllForList() {
        return langSprService.getAllForList();
    }
}
