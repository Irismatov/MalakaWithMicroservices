package com.malaka.aat.external.dto.test.without_answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionOptionDto {

    private String id;
    private String optionText;
    private Short hasImage;
    private String imgUrl;

}
