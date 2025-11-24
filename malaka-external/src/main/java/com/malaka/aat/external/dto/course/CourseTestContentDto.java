package com.malaka.aat.external.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseTestContentDto {
    private String id;
    private Integer duration;
    private Integer attemptLimit;
    private Integer attemptsLeft;
    private Integer passScore;
    private Integer questionCount;
    private Integer maxResult;

    private List<TestAttempt> attempts = new ArrayList<>();

    @Getter
    @Setter
    public static class TestAttempt {
        private String id;
        private Integer order;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;
        private Integer result;
        private Short isSuccess;
    }
}
