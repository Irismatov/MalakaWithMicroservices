package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.dto.auth.LoginOneIdRequest;
import com.malaka.aat.external.dto.auth.UserLoginDto;
import com.malaka.aat.external.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autentifikatsiya", description = "Tizimga kirish uchun API'lar")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Tizimga kirish",
            description = "Foydalanuvchi username va parol orqali tizimga kiradi. Muvaffaqiyatli kirish natijasida access va refresh tokenlar qaytariladi")
    @PostMapping("/login")
    public BaseResponse login(@RequestBody @Validated UserLoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/login/oneId")
    public BaseResponse loginWithOneId(@RequestBody @Validated LoginOneIdRequest request) {
        return authService.loginWithOneId(request);
    }

}
