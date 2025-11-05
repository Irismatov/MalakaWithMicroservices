package com.malaka.aat.internal.controller;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.internal.dto.auth.UserLoginDto;
import com.malaka.aat.internal.dto.auth.UserRegisterDto;
import com.malaka.aat.internal.service.AuthService;
import com.malaka.aat.internal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Autentifikatsiya", description = "Foydalanuvchilarni ro'yxatdan o'tkazish va tizimga kirish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "Foydalanuvchini ro'yxatdan o'tkazish",
            description = "Yangi foydalanuvchini ro'yxatdan o'tkazish. Username, parol, ism, familiya va boshqa ma'lumotlar kiritiladi")
    @PostMapping("/register")
    public BaseResponse register(@RequestBody @Validated UserRegisterDto dto) {
        return userService.save(dto);
    }

    @Operation(summary = "Tizimga kirish",
            description = "Foydalanuvchi username va parol orqali tizimga kiradi. Muvaffaqiyatli kirish natijasida access va refresh tokenlar qaytariladi")
    @PostMapping("/login")
    public BaseResponse login(@RequestBody @Validated UserLoginDto dto) {
        return authService.login(dto);
    }


}

