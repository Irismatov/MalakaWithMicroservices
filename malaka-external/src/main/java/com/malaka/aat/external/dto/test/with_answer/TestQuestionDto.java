package com.malaka.aat.external.dto.test.with_answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestQuestionDto {

    private String id;
    private String questionText;
    private Short hasImage;
    private String imgUrl;
    private List<QuestionOptionDto> options;


}
