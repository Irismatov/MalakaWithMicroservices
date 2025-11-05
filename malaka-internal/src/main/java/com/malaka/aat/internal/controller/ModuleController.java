package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.module.ModuleStateUpdateDto;
import com.malaka.aat.internal.dto.module.ModuleUpdateDto;
import com.malaka.aat.internal.dto.topic.TopicCreateDto;
import com.malaka.aat.internal.service.ModuleService;

@Tag(name = "Modullar boshqaruvi", description = "Kurs modullarini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RequestMapping("/api/module")
@RestController
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "Modul ma'lumotlarini yangilash",
            description = "Mavjud modulning ma'lumotlarini yangilash. Modul nomi, o'qituvchi va boshqa ma'lumotlarni o'zgartirish mumkin")
    @PutMapping("/{id}")
    public BaseResponse update(@PathVariable String id, @RequestBody @Validated ModuleUpdateDto dto) {
        return moduleService.update(id, dto);
    }

    @Operation(summary = "Modulni o'chirish",
            description = "Modulni soft delete qilish")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable String id) {
        return moduleService.delete(id);
    }

    @Operation(summary = "Modul holatini yangilash",
            description = "Modul holatini o'zgartirish: O'qituvchi YUBORILDI (002) holatiga o'tkazishi mumkin, Metodist TASDIQLASH (003) yoki RAD ETISH (004) mumkin")
    @PutMapping("/{id}/state")
    public BaseResponse updateState(@PathVariable String id, @RequestBody @Validated ModuleStateUpdateDto dto) {
        return moduleService.updateState(id, dto);
    }

    @Operation(summary = "Yangi mavzu yaratish",
            description = "O'qituvchi o'ziga tayinlangan modul uchun yangi mavzu yaratadi")
    @PostMapping(value = "/{id}/topic", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse createTopic(@PathVariable String id,@RequestBody @Validated TopicCreateDto dto) {
        return moduleService.createTopic(id, dto);
    }

    @GetMapping("/{id}")
    public BaseResponse getDetail(@PathVariable String id) {
        return moduleService.getById(id);
    }

}
