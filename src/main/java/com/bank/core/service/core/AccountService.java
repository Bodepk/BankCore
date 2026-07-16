package com.bank.core.service.core;

import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.dto.response.TransactionSummary;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio de cuentas bancarias.
 * TODOS los métodos devuelven DTOs, no Entities.
 *
 * Todas las operaciones reciben el usuario autenticado (currentUser) para
 * poder validar propiedad: un USER normal solo puede operar sobre sus
 * propias cuentas; un ADMIN puede operar sobre cualquier cuenta.
 */
public interface AccountService {

    // ===== OPERACIONES DE CUENTAS =====

    AccountResponse createAccount(User currentUser, String accountNumber, AccountType accountType, String currency);

    AccountResponse findByAccountNumber(User currentUser, String accountNumber);

    List<AccountResponse> findAllActiveAccounts(User currentUser);

    AccountResponse deposit(User currentUser, String accountNumber, BigDecimal amount);

    AccountResponse withdraw(User currentUser, String accountNumber, BigDecimal amount);

    AccountResponse transfer(User currentUser, String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    // Solo ADMIN
    AccountResponse blockAccount(User currentUser, String accountNumber);

    // Solo ADMIN
    AccountResponse activateAccount(User currentUser, String accountNumber);

    List<AccountResponse> findAccountsByType(User currentUser, AccountType accountType);

    // ===== OPERACIONES DE TRANSACCIONES =====

    List<TransactionResponse> getTransactions(User currentUser, String accountNumber);

    List<TransactionResponse> getTransactionsByDateRange(User currentUser, String accountNumber,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate);

    List<TransactionResponse> getRecentTransactions(User currentUser, String accountNumber, int limit);

    TransactionSummary getTransactionSummary(User currentUser, String accountNumber);
}
