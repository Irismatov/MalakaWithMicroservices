package com.malaka.aat.core.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseContentType {
    MAIN_CONTENT(0),
    LECTURE(1),
    PRESENTATION(2),
    TEST(3);

    private final int value;
}
