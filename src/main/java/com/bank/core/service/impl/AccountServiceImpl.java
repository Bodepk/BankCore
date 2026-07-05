package com.bank.core.service.impl;

import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.mapper.TransactionMapper;
import com.bank.core.domain.enums.AccountStatus;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.enums.TransactionType;
import com.bank.core.domain.exception.AccountNotFoundException;
import com.bank.core.domain.exception.InsufficientBalanceException;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.model.Transaction;
import com.bank.core.infrastructure.persistence.AccountRepository;
import com.bank.core.infrastructure.persistence.TransactionRepository;
import com.bank.core.service.core.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Account createAccount(String accountNumber, AccountType accountType, String currency) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("El número de cuenta " + accountNumber + " ya existe");
        }

        Account account = new Account(accountNumber, accountType, currency);
        if (accountType == AccountType.SAVINGS) {
            account.setInterestRate(new BigDecimal("2.5"));
        }

        return accountRepository.save(account);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Override
    public List<Account> findAllActiveAccounts() {
        return accountRepository.findByStatus(AccountStatus.ACTIVE);
    }

    @Override
    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo");
        }

        Account account = findByAccountNumber(accountNumber);
        BigDecimal balanceBefore = account.getBalance();

        account.deposit(amount);
        accountRepository.save(account);

        // Registrar la transacción
        createTransaction(account, null, TransactionType.DEPOSIT, amount,
                balanceBefore, account.getBalance(),
                "Depósito en cuenta " + accountNumber);

        return account;
    }

    @Override
    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }

        Account account = findByAccountNumber(accountNumber);

        if (!account.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(
                    accountNumber,
                    account.getBalance().add(account.getOverdraftLimit()),
                    amount
            );
        }

        BigDecimal balanceBefore = account.getBalance();
        account.withdraw(amount);
        accountRepository.save(account);

        // Registrar la transacción
        createTransaction(null, account, TransactionType.WITHDRAWAL, amount,
                balanceBefore, account.getBalance(),
                "Retiro de cuenta " + accountNumber);

        return account;
    }

    @Override
    @Transactional
    public Account transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transferencia debe ser positivo");
        }

        Account source = findByAccountNumber(sourceAccountNumber);
        Account destination = findByAccountNumber(destinationAccountNumber);

        if (!source.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(
                    sourceAccountNumber,
                    source.getBalance().add(source.getOverdraftLimit()),
                    amount
            );
        }

        BigDecimal sourceBalanceBefore = source.getBalance();
        BigDecimal destBalanceBefore = destination.getBalance();

        source.withdraw(amount);
        destination.deposit(amount);

        accountRepository.save(source);
        accountRepository.save(destination);

        // Registrar la transacción (transferencia)
        String description = String.format("Transferencia de %s a %s",
                sourceAccountNumber, destinationAccountNumber);
        createTransaction(source, destination, TransactionType.TRANSFER, amount,
                sourceBalanceBefore, source.getBalance(),
                description);

        return source;
    }

    @Override
    @Transactional
    public Account blockAccount(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        account.setStatus(AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account activateAccount(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    @Override
    public List<TransactionResponse> getTransactions(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        List<Transaction> transactions = transactionRepository.findByAccount(account);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para crear y guardar una transacción.
     */
    private void createTransaction(Account source, Account destination,
                                   TransactionType type, BigDecimal amount,
                                   BigDecimal balanceBefore, BigDecimal balanceAfter,
                                   String description) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }
}