package com.bank.core.api.controller;

import com.bank.core.api.dto.request.CreateAccountRequest;
import com.bank.core.api.dto.request.DepositRequest;
import com.bank.core.api.dto.request.TransferRequest;
import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.dto.response.TransactionSummary;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.service.core.AccountService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(
                request.getAccountNumber(),
                request.getAccountType(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        AccountResponse response = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllActiveAccounts() {
        List<AccountResponse> responses = accountService.findAllActiveAccounts();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        AccountResponse response = accountService.deposit(accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        AccountResponse response = accountService.withdraw(accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<AccountResponse> transfer(@Valid @RequestBody TransferRequest request) {
        AccountResponse response = accountService.transfer(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable String accountNumber) {
        List<TransactionResponse> transactions = accountService.getTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/transactions/filter")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<TransactionResponse> transactions = accountService
                .getTransactionsByDateRange(accountNumber, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/transactions/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "10") int limit) {

        List<TransactionResponse> transactions = accountService
                .getRecentTransactions(accountNumber, limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/summary")
    public ResponseEntity<TransactionSummary> getTransactionSummary(
            @PathVariable String accountNumber) {

        TransactionSummary summary = accountService.getTransactionSummary(accountNumber);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<AccountResponse>> findAccountsByType(
            @RequestParam AccountType type) {

        List<AccountResponse> responses = accountService.findAccountsByType(type);
        return ResponseEntity.ok(responses);
    }
}