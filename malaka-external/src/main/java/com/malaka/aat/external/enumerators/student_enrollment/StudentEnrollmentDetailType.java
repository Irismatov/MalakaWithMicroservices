package com.malaka.aat.external.enumerators.student_enrollment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentEnrollmentDetailType {

    START(0), FINISH(1);

    private final int value;
}
