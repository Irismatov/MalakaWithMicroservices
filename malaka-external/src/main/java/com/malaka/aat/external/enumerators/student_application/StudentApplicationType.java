package com.malaka.aat.external.enumerators.student_application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StudentApplicationType {
    INDIVIDUAL(0), CORPORATE(1);

    private final int value;
}
