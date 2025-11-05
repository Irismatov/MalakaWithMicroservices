package com.malaka.aat.internal.dto.test;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TestQuestionUpdateDto {
    private MultipartFile img;
    private String questionText;
}
