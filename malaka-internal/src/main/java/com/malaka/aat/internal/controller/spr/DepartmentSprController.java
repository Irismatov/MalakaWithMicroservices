package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprCreateDto;
import com.malaka.aat.internal.dto.spr.department.DepartmentSprUpdateDto;
import com.malaka.aat.internal.service.spr.DepartmentSprService;

@Tag(name = "Kafedralar ma'lumotnomalari", description = "Kafedralar ma'lumotnomalarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr")
public class DepartmentSprController {

    private final DepartmentSprService departmentSprService;

    @Operation(summary = "Yangi kafedra yaratish",
            description = "Yangi kafedra ma'lumotnomasi yaratish")
    @PostMapping("/department")
    public BaseResponse save(@RequestBody @Validated DepartmentSprCreateDto dto) {
        return departmentSprService.save(dto);
    }

    @Operation(summary = "Kafedralar ro'yxatini olish",
            description = "Sahifalash va fakultet bo'yicha filtrlash bilan kafedralar ro'yxatini olish")
    @GetMapping("/department")
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String facultyId
    ) {
        return departmentSprService.getList(page, size, facultyId);
    }

    @Operation(summary = "Kafedr ani ID bo'yicha olish",
            description = "Kafedra ma'lumotlarini ID orqali olish")
    @GetMapping("/department/{id}")
    public BaseResponse getById(@PathVariable String id) {
        return departmentSprService.getById(id);
    }

    @Operation(summary = "Kafedra ma'lumotlarini yangilash",
            description = "Mavjud kafedraning ma'lumotlarini yangilash")
    @PutMapping("/department/{id}")
    public ResponseWithPagination update(
            @PathVariable String id,
            @RequestBody @Validated DepartmentSprUpdateDto dto
    ) {
        return departmentSprService.update(id, dto);
    }

    @Operation(summary = "Kafedr ani o'chirish",
            description = "Kafedr ani soft delete qilish")
    @DeleteMapping("/department/{id}")
    public ResponseWithPagination delete(@PathVariable String id) {
        return departmentSprService.delete(id);
    }

    @Operation(summary = "Barcha kafedralarni ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha kafedralarni sahifalashsiz olish. Fakultet bo'yicha filtrlash mumkin")
    @GetMapping("/departments")
    public BaseResponse getAllForList(@RequestParam(required = false) String facultyId) {
        return departmentSprService.getAllForList(facultyId);
    }
}
