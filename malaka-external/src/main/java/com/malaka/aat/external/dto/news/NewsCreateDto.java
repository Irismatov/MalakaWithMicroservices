package com.malaka.aat.external.dto.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class NewsCreateDto {
    @NotBlank(message = "News title must be provided")
    @Size(min = 3, max = 50, message = "The length of the title must be between 3 and 50")
    private String title;
    @NotBlank(message = "News text must be provided")
    @Size(min = 3, max = 1000, message = "The length of the text must be between 3 and 1000")
    private String text;
    @NotNull(message = "Image must be provided")
    private MultipartFile image;
}
