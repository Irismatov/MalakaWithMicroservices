package com.malaka.aat.external.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TestAttemptState {
    STARTED(0), FINISHED(1);

    private final int value;
}
