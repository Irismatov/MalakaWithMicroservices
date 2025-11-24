package com.malaka.aat.external.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TestAttemptType {
    TOPIC_TEST(0), FINAL_TEST(1);

    private final int value;
}
