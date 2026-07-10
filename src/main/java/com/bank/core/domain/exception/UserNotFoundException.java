package com.bank.core.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
    }

    public UserNotFoundException(String username, Throwable cause) {
        super("Usuario no encontrado: " + username, cause);
    }
}