package com.malaka.aat.external.dto.test.without_answer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestAttemptDto {
    private List<TestAttemptDtoItem> answers;
}
