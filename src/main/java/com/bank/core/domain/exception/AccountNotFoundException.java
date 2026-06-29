package com.bank.core.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una cuenta.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Cuenta no encontrada con número: " + accountNumber);
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super("Cuenta no encontrada con número: " + accountNumber, cause);
    }
}