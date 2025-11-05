package com.malaka.aat.external.enumerators.student_enrollment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StudentEnrollmentStatus {
    STARTED(0), FINISHED(1), EXPIRED(2);

    private final int value;
}
