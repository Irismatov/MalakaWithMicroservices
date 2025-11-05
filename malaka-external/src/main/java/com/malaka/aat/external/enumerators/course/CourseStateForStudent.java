package com.malaka.aat.external.enumerators.course;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CourseStateForStudent {

    NEW(0), ENROLLING(1), FINISHED(2), EXPIRED(3);

    private final int value;

}
