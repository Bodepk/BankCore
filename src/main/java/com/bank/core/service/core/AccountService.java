package com.bank.core.service.core;

import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz del servicio de cuentas bancarias.
 * Define todas las operaciones que se pueden realizar con cuentas.
 */
public interface AccountService {

    /**
     * Crea una nueva cuenta bancaria.
     */
    Account createAccount(String accountNumber, AccountType accountType, String currency);

    /**
     * Busca una cuenta por su número.
     */
    Account findByAccountNumber(String accountNumber);

    /**
     * Lista todas las cuentas activas.
     */
    List<Account> findAllActiveAccounts();

    /**
     * Deposita dinero en una cuenta.
     */
    Account deposit(String accountNumber, BigDecimal amount);

    /**
     * Retira dinero de una cuenta.
     */
    Account withdraw(String accountNumber, BigDecimal amount);

    /**
     * Transfiere dinero entre dos cuentas.
     */
    Account transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    /**
     * Bloquea una cuenta.
     */
    Account blockAccount(String accountNumber);

    /**
     * Activa una cuenta.
     */
    Account activateAccount(String accountNumber);
}