package com.malaka.aat.external.dto.course.internal;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionOptionDto {

    private String id;
    private String optionText;
    private Short isCorrect;
    private Short hasImage;
    private String imgUrl;
}
