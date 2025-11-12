package com.malaka.aat.external.dto.test.attempt;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestAttemptResponseDtoItem {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer correctAnswerPercentage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private Integer attemptNumber;

}
