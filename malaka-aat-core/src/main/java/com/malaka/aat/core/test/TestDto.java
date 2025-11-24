package com.malaka.aat.core.test;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestDto {
    private String id;
    private Integer attemptLimit;
    private Integer duration;
    private List<Question> questions;

    @Getter
    @Setter
    public static class Question {
        private String id;
        private String text;
        private List<Option> options;
    }

    @Getter
    @Setter
    public static class Option {
        private String id;
        private String text;
    }
}
