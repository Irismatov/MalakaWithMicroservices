package com.malaka.aat.external.enumerators.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    OTHER(0), MALE(1), FEMALE(2), UNKNOWN(3);

    private final int value;
}