package com.malaka.aat.external.enumerators.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PassportType {
    UNKNOWN(0), BIRTH_CERT(1), BIOMETRIC(2), ID_CARD(3), FOREIGN(4), ;

    private final int value;
}