package com.malaka.aat.internal.dto.test;

import com.malaka.aat.internal.model.Test;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TestDto {

    private String id;
    private Integer attemptLimit;
    private Integer durationInMinutes;
    private List<TestQuestionDto> questions;

    public TestDto(Test test) {
        this.id = test.getId();
        this.attemptLimit = test.getAttemptLimit();

        if (test.getQuestions() != null) {
            this.questions = test.getQuestions().stream()
                    .map(TestQuestionDto::new)
                    .collect(Collectors.toList());
        }
    }

    // Constructor without questions (for list views)
    public TestDto(Test test, boolean includeQuestions) {
        this(test);
        if (!includeQuestions) {
            this.questions = null;
        }
    }
}
