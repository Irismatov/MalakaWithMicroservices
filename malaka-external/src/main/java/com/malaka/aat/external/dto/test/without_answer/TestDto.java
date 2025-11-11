package com.malaka.aat.external.dto.test.without_answer;

import com.malaka.aat.external.dto.test.TestQuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestDto {

    private String id;
    private Integer attemptLimit;
    private List<TestQuestionDto> questions;

}
