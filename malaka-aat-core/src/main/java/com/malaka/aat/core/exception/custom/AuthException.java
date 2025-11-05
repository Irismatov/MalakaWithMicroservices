package com.malaka.aat.core.exception.custom;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("You are not authorized to do this action");
    }

    public AuthException(String message) {
        super(message);
    }
}
