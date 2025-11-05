package com.malaka.aat.internal.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.malaka.aat.internal.dto.user.UserCreateDto;
import com.malaka.aat.internal.dto.user.UserFilterDto;
import com.malaka.aat.internal.dto.user.UserUpdateDto;
import com.malaka.aat.internal.service.UserService;


@Tag(name = "Foydalanuvchilar boshqaruvi", description = "Foydalanuvchilarni boshqarish uchun API'lar")
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "Joriy foydalanuvchi ma'lumotlarini olish",
            description = "Tizimga kirgan foydalanuvchining o'z ma'lumotlarini olish")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public BaseResponse getMe() {
        return userService.getMe();
    }

    @Operation(summary = "Yangi foydalanuvchi yaratish",
            description = "Tizimda yangi foydalanuvchi yaratish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public BaseResponse createUser(@RequestBody @Validated UserCreateDto dto) {
        return userService.createUser(dto);
    }


    @Operation(summary = "Foydalanuvchilar ro'yxatini olish",
            description = "Sahifalash va filtrlash bilan foydalanuvchilar ro'yxatini olish. METHODIST, ADMIN va SUPER_ADMIN ruxsat etilgan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseWithPagination getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Valid @ModelAttribute UserFilterDto filterDto
    ) {
        return userService.getList(page, size, filterDto);
    }

    @Operation(summary = "Foydalanuvchilarni ro'yxat shaklida olish",
            description = "Ma'lumotnoma uchun foydalanuvchilar ro'yxatini olish. Sahifalashsiz, filtrlash bilan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN', 'TEACHER', 'FACULTY_HEAD', 'DEPARTMENT_HEAD')")
    @GetMapping("/list")
    public BaseResponse getUsersForReference(
            @Valid @ModelAttribute UserFilterDto filterDto
    ) {
        return userService.getUsersForReference(filterDto);
    }

    @Operation(summary = "Foydalanuvchini ID bo'yicha olish",
            description = "Foydalanuvchining barcha ma'lumotlarini ID orqali olish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable String id) {
        return userService.getByIdDto(id);
    }

    @Operation(summary = "Foydalanuvchi ma'lumotlarini yangilash",
            description = "Mavjud foydalanuvchining ma'lumotlarini yangilash. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseWithPagination update(
            @PathVariable String id,
            @RequestBody @Validated UserUpdateDto dto
    ) {
        return userService.update(id, dto);
    }

    @Operation(summary = "Foydalanuvchini o'chirish",
            description = "Foydalanuvchini soft delete qilish. Faqat ADMIN va SUPER_ADMIN ruxsat etilgan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseWithPagination delete(@PathVariable String id) {
        return userService.delete(id);
    }

    @Operation(summary = "O'qituvchilar ro'yxatini olish",
            description = "TEACHER roliga ega barcha foydalanuvchilarni olish")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN', 'FACULTY_HEAD', 'DEPARTMENT_HEAD')")
    @GetMapping("/teachers")
    public BaseResponse getTeachers() {
        return userService.getTeachers();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'METHODIST')")
    @GetMapping("/students/type/{type}")
    public BaseResponse getStudentList(@PathVariable Long type) {
        return userService.getStudents(type);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'METHODIST')")
    @GetMapping("/students/course/{courseId}/type/{type}")
    public BaseResponse getStudentListByCourseIdAndType(@PathVariable String courseId, @PathVariable Long type) {
        return userService.getStudentsByCourseIdAndType(courseId, type);
    }

}
