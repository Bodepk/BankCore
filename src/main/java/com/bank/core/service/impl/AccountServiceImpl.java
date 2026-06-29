package com.bank.core.service.impl;

import com.bank.core.domain.enums.AccountStatus;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.exception.AccountNotFoundException;
import com.bank.core.domain.exception.InsufficientBalanceException;
import com.bank.core.domain.model.Account;
import com.bank.core.infrastructure.persistence.AccountRepository;
import com.bank.core.service.core.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio de cuentas bancarias.
 *
 * @Service: Marca esta clase como un bean de servicio en Spring
 * @Transactional: Todas las operaciones son transaccionales
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    /**
     * Constructor con inyección de dependencias.
     * Spring inyecta automáticamente AccountRepository.
     */
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(String accountNumber, AccountType accountType, String currency) {
        // Validar que el número de cuenta no exista
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("El número de cuenta " + accountNumber + " ya existe");
        }

        // Crear la nueva cuenta
        Account account = new Account(accountNumber, accountType, currency);

        // Configurar tasa de interés según el tipo de cuenta
        if (accountType == AccountType.SAVINGS) {
            account.setInterestRate(new BigDecimal("2.5")); // 2.5% anual
        }

        // Guardar en la base de datos
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
        // Validar el monto
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo");
        }

        // Buscar la cuenta
        Account account = findByAccountNumber(accountNumber);

        // Realizar el depósito
        account.deposit(amount);

        // Guardar los cambios
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        // Validar el monto
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }

        // Buscar la cuenta
        Account account = findByAccountNumber(accountNumber);

        // Verificar si tiene saldo suficiente
        if (!account.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(
                    accountNumber,
                    account.getBalance().add(account.getOverdraftLimit()),
                    amount
            );
        }

        // Realizar el retiro
        account.withdraw(amount);

        // Guardar los cambios
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        // Validar que no sea la misma cuenta
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        // Validar el monto
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transferencia debe ser positivo");
        }

        // Buscar ambas cuentas
        Account source = findByAccountNumber(sourceAccountNumber);
        Account destination = findByAccountNumber(destinationAccountNumber);

        // Verificar que la cuenta origen tiene suficiente saldo
        if (!source.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(
                    sourceAccountNumber,
                    source.getBalance().add(source.getOverdraftLimit()),
                    amount
            );
        }

        // Realizar la transferencia (atómica)
        source.withdraw(amount);
        destination.deposit(amount);

        // Guardar ambas cuentas
        accountRepository.save(source);
        accountRepository.save(destination);

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
}