package com.malaka.aat.external.dto.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestDto {

    private String id;
    private List<TestQuestionDto> questions;

}
