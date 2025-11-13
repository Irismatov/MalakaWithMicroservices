package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.dto.info.GetInfoRequest;
import com.malaka.aat.external.service.InfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Fuqaro ma'lumotlari", description = "E-Gov orqali fuqaro ma'lumotlarini olish uchun API'lar")
@RestController
@RequestMapping("/api/external/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;


    @Operation(summary = "PINFL bo'yicha fuqaro ma'lumotlarini olish",
            description = "E-Gov GCP API orqali PINFL (14 raqamli shaxsiy identifikatsiya raqami) bo'yicha fuqaro ma'lumotlarini olish. Natija keshlanadi")
    @PostMapping("/pinfl")
    public BaseResponse getInfoFromPinfl(@RequestBody @Valid GetInfoRequest pinfl) {
        return infoService.getInfoFromPinfl(pinfl);
    }

}
