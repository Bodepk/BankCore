package com.bank.core.api.controller;

import com.bank.core.api.dto.request.CreateAccountRequest;
import com.bank.core.api.dto.request.DepositRequest;
import com.bank.core.api.dto.request.TransferRequest;
import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.mapper.AccountMapper;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.model.Transaction;
import com.bank.core.service.core.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones con cuentas.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    /**
     * Crea una nueva cuenta bancaria.
     * POST /api/v1/accounts
     * Body: { "accountNumber": "1234567890", "accountType": "SAVINGS", "currency": "USD" }
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(
                request.getAccountNumber(),
                request.getAccountType(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountMapper.toResponse(account));
    }

    /**
     * Busca una cuenta por su número.
     * GET /api/v1/accounts/{accountNumber}
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    /**
     * Lista todas las cuentas activas.
     * GET /api/v1/accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllActiveAccounts() {
        List<Account> accounts = accountService.findAllActiveAccounts();
        List<AccountResponse> responses = accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Deposita dinero en una cuenta.
     * POST /api/v1/accounts/{accountNumber}/deposit
     * Body: { "amount": 1000 }
     */
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        Account account = accountService.deposit(accountNumber, request.getAmount());
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    /**
     * Retira dinero de una cuenta.
     * POST /api/v1/accounts/{accountNumber}/withdraw
     * Body: { "amount": 500 }
     */
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        Account account = accountService.withdraw(accountNumber, request.getAmount());
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable String accountNumber) {
        List<TransactionResponse> transactions = accountService.getTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
    /**
     * Transfiere dinero entre dos cuentas.
     * POST /api/v1/accounts/transfer
     * Body: { "sourceAccountNumber": "1234567890", "destinationAccountNumber": "9876543210", "amount": 100 }
     */
    @PostMapping("/transfer")
    public ResponseEntity<AccountResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Account account = accountService.transfer(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }
}