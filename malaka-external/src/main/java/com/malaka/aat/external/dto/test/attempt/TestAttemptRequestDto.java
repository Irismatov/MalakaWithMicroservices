package com.malaka.aat.external.dto.test.attempt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestAttemptRequestDto {
    private List<TestAttemptRequestDtoItem> answers;
}
