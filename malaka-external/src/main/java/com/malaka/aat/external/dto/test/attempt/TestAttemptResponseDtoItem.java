package com.malaka.aat.external.dto.test.attempt;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestAttemptResponseDtoItem {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer correctAnswersPercentage;
    private LocalDateTime time;
    private Integer attemptNumber;

}
