package com.malaka.aat.external.dto.test.attempt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestAttemptResponseDto {
    private Integer totalAttempts;
    private Integer attemptsLeft;
    private List<TestAttemptResponseDtoItem> attempts;
}
