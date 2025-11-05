package com.malaka.aat.core.exception.custom;

public class LoginException extends RuntimeException {
    public LoginException() {
        super("Username or password is incorrect");
    }
}
