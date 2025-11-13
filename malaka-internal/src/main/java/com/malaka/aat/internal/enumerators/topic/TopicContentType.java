package com.malaka.aat.internal.enumerators.topic;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TopicContentType {
    VIDEO(0),
    AUDIO(1),
    ZOOM(2),
    CONFIDENTIAL(3);


    private final int value;

    public static TopicContentType getFromValue(int value) {
        return TopicContentType.values()[value];
    }
}
