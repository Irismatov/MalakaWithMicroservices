package com.malaka.aat.core.exception.custom;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
