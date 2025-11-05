package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprCreateDto;
import com.malaka.aat.internal.dto.spr.faculty.FacultySprUpdateDto;
import com.malaka.aat.internal.service.spr.FacultySprService;

@Tag(name = "Fakultetlar ma'lumotnomalari", description = "Fakultetlar ma'lumotnomalarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr")
public class FacultySprController {

    private final FacultySprService facultySprService;

    @Operation(summary = "Yangi fakultet yaratish",
            description = "Yangi fakultet ma'lumotnomasi yaratish")
    @PostMapping("/faculty")
    public BaseResponse save(@RequestBody @Validated FacultySprCreateDto dto) {
        return facultySprService.save(dto);
    }

    @Operation(summary = "Fakultetlar ro'yxatini olish",
            description = "Sahifalash bilan fakultetlar ro'yxatini olish")
    @GetMapping("/faculty")
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return facultySprService.getList(page, size);
    }

    @Operation(summary = "Fakultetni ID bo'yicha olish",
            description = "Fakultet ma'lumotlarini ID orqali olish")
    @GetMapping("/faculty/{id}")
    public BaseResponse getById(@PathVariable String id) {
        return facultySprService.getById(id);
    }

    @Operation(summary = "Fakultet ma'lumotlarini yangilash",
            description = "Mavjud fakultetning ma'lumotlarini yangilash")
    @PutMapping("/faculty/{id}")
    public ResponseWithPagination update(
            @PathVariable String id,
            @RequestBody @Validated FacultySprUpdateDto dto
    ) {
        return facultySprService.update(id, dto);
    }

    @Operation(summary = "Fakultetni o'chirish",
            description = "Fakultetni soft delete qilish")
    @DeleteMapping("/faculty/{id}")
    public ResponseWithPagination delete(@PathVariable String id) {
        return facultySprService.delete(id);
    }

    @Operation(summary = "Barcha fakultetlarni ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha fakultetlarni sahifalashsiz olish")
    @GetMapping("/faculties")
    public BaseResponse getAllForList() {
        return facultySprService.getAllForList();
    }

}
