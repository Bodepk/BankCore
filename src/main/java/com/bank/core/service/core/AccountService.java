package com.bank.core.service.core;

import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(String accountNumber, AccountType accountType, String currency);

    Account findByAccountNumber(String accountNumber);

    List<Account> findAllActiveAccounts();

    Account deposit(String accountNumber, BigDecimal amount);

    Account withdraw(String accountNumber, BigDecimal amount);

    Account transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    Account blockAccount(String accountNumber);

    Account activateAccount(String accountNumber);

    // NUEVO: Obtener transacciones como DTOs
    List<TransactionResponse> getTransactions(String accountNumber);
}