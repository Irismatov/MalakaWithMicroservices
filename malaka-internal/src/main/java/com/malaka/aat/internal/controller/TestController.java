package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.dto.test.TestQuestionUpdateDto;
import com.malaka.aat.internal.service.TestService;

import java.io.IOException;

@Tag(name = "Testlar boshqaruvi", description = "Testlar va savollarni boshqarish uchun API'lar")
@RequiredArgsConstructor
@RequestMapping("/api/test")
@RestController
public class TestController {

    private final TestService testService;

    @Operation(summary = "Word faylidan test yaratish",
            description = "Mavzu uchun test yaratish uchun savollar bilan Word (.docx) faylini yuklash. " +
                    "Format: Savol raqami, savol matni, javob variantlari (A/B/C/D/E yoki 1/2/3/4/5), to'g'ri javob qatori")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse createTestFromWord(
            @RequestParam("file") MultipartFile file) throws IOException {
        return testService.createTestFromWord(file);
    }

    @PutMapping("/question/{questionId}")
    public BaseResponse updateQuestion(@PathVariable String questionId, @ModelAttribute TestQuestionUpdateDto dto) {
        return testService.updateQuestion(questionId, dto);
    }

}
