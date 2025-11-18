package com.malaka.aat.external.enumerators.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE(0), FEMALE(1), UNKNOWN(2);

    private final int value;
}