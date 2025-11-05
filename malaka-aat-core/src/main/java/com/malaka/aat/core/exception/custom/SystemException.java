package com.malaka.aat.core.exception.custom;

public class SystemException extends RuntimeException {
    public SystemException() {
        super("Internal server error");
    }
    public SystemException(String message) {
        super(message);
    }
}
