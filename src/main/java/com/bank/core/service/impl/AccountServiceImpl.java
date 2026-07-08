package com.bank.core.service.impl;

import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.dto.response.TransactionSummary;
import com.bank.core.api.mapper.AccountMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              AccountMapper accountMapper,
                              TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

    // =============================================
    // OPERACIONES DE CUENTAS - DEVUELVEN AccountResponse
    // =============================================

    @Override
    public AccountResponse createAccount(String accountNumber, AccountType accountType, String currency) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("El número de cuenta " + accountNumber + " ya existe");
        }

        Account account = new Account(accountNumber, accountType, currency);
        if (accountType == AccountType.SAVINGS) {
            account.setInterestRate(new BigDecimal("2.5"));
        }

        Account saved = accountRepository.save(account);
        log.info("Cuenta creada: {} - Tipo: {} - Moneda: {}", accountNumber, accountType, currency);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponse findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> findAllActiveAccounts() {
        List<Account> accounts = accountRepository.findByStatus(AccountStatus.ACTIVE);
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse deposit(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo");
        }

        Account account = findByAccountNumberEntity(accountNumber);
        BigDecimal balanceBefore = account.getBalance();

        account.deposit(amount);
        accountRepository.save(account);

        createTransaction(account, null, TransactionType.DEPOSIT, amount,
                balanceBefore, account.getBalance(),
                "Depósito en cuenta " + accountNumber);

        log.info("Depósito: {} en cuenta {} - Nuevo saldo: {}", amount, accountNumber, account.getBalance());
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse withdraw(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }

        Account account = findByAccountNumberEntity(accountNumber);

        if (!account.hasSufficientBalance(amount)) {
            log.warn("Intento de retiro sin saldo suficiente: {} - Disponible: {}, Solicitado: {}",
                    accountNumber, account.getBalance().add(account.getOverdraftLimit()), amount);
            throw new InsufficientBalanceException(
                    accountNumber,
                    account.getBalance().add(account.getOverdraftLimit()),
                    amount
            );
        }

        BigDecimal balanceBefore = account.getBalance();
        account.withdraw(amount);
        accountRepository.save(account);

        createTransaction(null, account, TransactionType.WITHDRAWAL, amount,
                balanceBefore, account.getBalance(),
                "Retiro de cuenta " + accountNumber);

        log.info("Retiro: {} de cuenta {} - Nuevo saldo: {}", amount, accountNumber, account.getBalance());
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transferencia debe ser positivo");
        }

        Account source = findByAccountNumberEntity(sourceAccountNumber);
        Account destination = findByAccountNumberEntity(destinationAccountNumber);

        if (!source.hasSufficientBalance(amount)) {
            log.warn("Intento de transferencia sin saldo suficiente: {} - Disponible: {}, Solicitado: {}",
                    sourceAccountNumber, source.getBalance().add(source.getOverdraftLimit()), amount);
            throw new InsufficientBalanceException(
                    sourceAccountNumber,
                    source.getBalance().add(source.getOverdraftLimit()),
                    amount
            );
        }

        BigDecimal sourceBalanceBefore = source.getBalance();

        source.withdraw(amount);
        destination.deposit(amount);

        accountRepository.save(source);
        accountRepository.save(destination);

        String description = String.format("Transferencia de %s a %s",
                sourceAccountNumber, destinationAccountNumber);
        createTransaction(source, destination, TransactionType.TRANSFER, amount,
                sourceBalanceBefore, source.getBalance(),
                description);

        log.info("Transferencia: {} de {} a {} - Nuevo saldo origen: {}",
                amount, sourceAccountNumber, destinationAccountNumber, source.getBalance());
        return accountMapper.toResponse(source);
    }

    @Override
    public AccountResponse blockAccount(String accountNumber) {
        Account account = findByAccountNumberEntity(accountNumber);
        account.setStatus(AccountStatus.BLOCKED);
        Account saved = accountRepository.save(account);
        log.info("Cuenta bloqueada: {}", accountNumber);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponse activateAccount(String accountNumber) {
        Account account = findByAccountNumberEntity(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        Account saved = accountRepository.save(account);
        log.info("Cuenta activada: {}", accountNumber);
        return accountMapper.toResponse(saved);
    }

    @Override
    public List<AccountResponse> findAccountsByType(AccountType accountType) {
        List<Account> accounts = accountRepository.findByAccountType(accountType);
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    // =============================================
    // OPERACIONES DE TRANSACCIONES - DEVUELVEN TransactionResponse
    // =============================================

    @Override
    public List<TransactionResponse> getTransactions(String accountNumber) {
        Account account = findByAccountNumberEntity(accountNumber);
        List<Transaction> transactions = transactionRepository.findByAccount(account);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByDateRange(String accountNumber,
                                                                LocalDateTime startDate,
                                                                LocalDateTime endDate) {
        Account account = findByAccountNumberEntity(accountNumber);
        List<Transaction> transactions = transactionRepository
                .findByAccountAndDateRange(account, startDate, endDate);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getRecentTransactions(String accountNumber, int limit) {
        Account account = findByAccountNumberEntity(accountNumber);
        List<Transaction> transactions = transactionRepository
                .findRecentTransactions(account, limit);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionSummary getTransactionSummary(String accountNumber) {
        Account account = findByAccountNumberEntity(accountNumber);
        List<Transaction> transactions = transactionRepository.findByAccount(account);

        TransactionSummary summary = new TransactionSummary();

        long deposits = 0;
        long withdrawals = 0;
        long transfers = 0;
        BigDecimal depositAmount = BigDecimal.ZERO;
        BigDecimal withdrawalAmount = BigDecimal.ZERO;
        BigDecimal transferAmount = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            switch (t.getTransactionType()) {
                case DEPOSIT:
                    deposits++;
                    depositAmount = depositAmount.add(t.getAmount());
                    break;
                case WITHDRAWAL:
                    withdrawals++;
                    withdrawalAmount = withdrawalAmount.add(t.getAmount());
                    break;
                case TRANSFER:
                    transfers++;
                    // Solo contamos transferencias donde la cuenta es origen
                    if (t.getSourceAccount() != null &&
                            t.getSourceAccount().getAccountNumber().equals(accountNumber)) {
                        transferAmount = transferAmount.add(t.getAmount());
                    }
                    break;
                default:
                    break;
            }
        }

        summary.setTotalDeposits(deposits);
        summary.setTotalWithdrawals(withdrawals);
        summary.setTotalTransfers(transfers);
        summary.setTotalDepositAmount(depositAmount);
        summary.setTotalWithdrawalAmount(withdrawalAmount);
        summary.setTotalTransferAmount(transferAmount);
        summary.setNetBalanceChange(
                depositAmount.subtract(withdrawalAmount).subtract(transferAmount)
        );

        return summary;
    }

    // =============================================
    // MÉTODOS AUXILIARES PRIVADOS
    // =============================================

    /**
     * Método interno para obtener la entidad Account completa.
     * Solo se usa dentro del Service.
     * Este método es PRIVADO y NO se expone en la interfaz.
     */
    private Account findByAccountNumberEntity(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    /**
     * Crea y guarda una transacción en la base de datos.
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
        log.debug("Transacción creada: {} - {}", transaction.getTransactionId(), type);
    }
}