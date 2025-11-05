package com.malaka.aat.internal.controller.spr;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.spr.role.RoleCreateDto;
import com.malaka.aat.internal.dto.spr.role.RoleUpdateDto;
import com.malaka.aat.internal.service.spr.RoleService;

@Tag(name = "Rollar ma'lumotnomalari", description = "Foydalanuvchi rollarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/spr/role")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Yangi rol yaratish",
            description = "Yangi foydalanuvchi roli yaratish")
    @PostMapping
    public BaseResponse save(@RequestBody @Validated RoleCreateDto dto) {
        return roleService.save(dto);
    }

    @Operation(summary = "Rollar ro'yxatini olish",
            description = "Sahifalash bilan rollar ro'yxatini olish")
    @GetMapping
    public ResponseWithPagination getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return roleService.getList(page, size);
    }

    @Operation(summary = "Rolni ID bo'yicha olish",
            description = "Rol ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable String id) {
        return roleService.getById(id);
    }

    @Operation(summary = "Rol ma'lumotlarini yangilash",
            description = "Mavjud rolning ma'lumotlarini yangilash")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable String id,
            @RequestBody @Validated RoleUpdateDto dto
    ) {
        return roleService.update(id, dto);
    }

    @Operation(summary = "Rolni o'chirish",
            description = "Rolni soft delete qilish")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable String id) {
        return roleService.delete(id);
    }

    @Operation(summary = "Barcha rollarni ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun barcha rollarni sahifalashsiz olish")
    @GetMapping("/list")
    public BaseResponse getAllForList() {
        return roleService.getAllForList();
    }
}
