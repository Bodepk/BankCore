package com.bank.core.domain.exception;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando una cuenta no tiene saldo suficiente.
 */
public class InsufficientBalanceException extends RuntimeException {

    private final String accountNumber;
    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    public InsufficientBalanceException(String accountNumber, BigDecimal availableBalance, BigDecimal requestedAmount) {
        super(String.format("Saldo insuficiente en cuenta %s. Disponible: %s, Solicitado: %s",
                accountNumber, availableBalance, requestedAmount));
        this.accountNumber = accountNumber;
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}