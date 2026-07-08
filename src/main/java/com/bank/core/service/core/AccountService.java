package com.bank.core.service.core;

import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.dto.response.TransactionSummary;
import com.bank.core.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio de cuentas bancarias.
 * TODOS los métodos devuelven DTOs, no Entities.
 */
public interface AccountService {

    // ===== OPERACIONES DE CUENTAS =====

    AccountResponse createAccount(String accountNumber, AccountType accountType, String currency);

    AccountResponse findByAccountNumber(String accountNumber);

    List<AccountResponse> findAllActiveAccounts();

    AccountResponse deposit(String accountNumber, BigDecimal amount);

    AccountResponse withdraw(String accountNumber, BigDecimal amount);

    AccountResponse transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    AccountResponse blockAccount(String accountNumber);

    AccountResponse activateAccount(String accountNumber);

    List<AccountResponse> findAccountsByType(AccountType accountType);

    // ===== OPERACIONES DE TRANSACCIONES =====

    List<TransactionResponse> getTransactions(String accountNumber);

    List<TransactionResponse> getTransactionsByDateRange(String accountNumber,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate);

    List<TransactionResponse> getRecentTransactions(String accountNumber, int limit);

    TransactionSummary getTransactionSummary(String accountNumber);
}