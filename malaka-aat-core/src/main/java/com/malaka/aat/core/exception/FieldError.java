package com.malaka.aat.core.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {
    private String field;
    private String message;
}
