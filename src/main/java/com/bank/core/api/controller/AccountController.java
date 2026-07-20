package com.bank.core.api.controller;

import com.bank.core.api.dto.request.CreateAccountRequest;
import com.bank.core.api.dto.request.DepositRequest;
import com.bank.core.api.dto.request.TransferRequest;
import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.api.dto.response.TransactionSummary;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.User;
import com.bank.core.service.core.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Cuentas y Transacciones", description = "Creación de cuentas, depósitos, retiros, transferencias e historial")
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse response = accountService.createAccount(
                currentUser,
                request.getAccountNumber(),
                request.getAccountType(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {

        AccountResponse response = accountService.findByAccountNumber(currentUser, accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllActiveAccounts(
            @AuthenticationPrincipal User currentUser) {

        List<AccountResponse> responses = accountService.findAllActiveAccounts(currentUser);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        AccountResponse response = accountService.deposit(currentUser, accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        AccountResponse response = accountService.withdraw(currentUser, accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<AccountResponse> transfer(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TransferRequest request) {

        AccountResponse response = accountService.transfer(
                currentUser,
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {

        List<TransactionResponse> transactions = accountService.getTransactions(currentUser, accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/transactions/filter")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<TransactionResponse> transactions = accountService
                .getTransactionsByDateRange(currentUser, accountNumber, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/transactions/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "10") int limit) {

        List<TransactionResponse> transactions = accountService
                .getRecentTransactions(currentUser, accountNumber, limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountNumber}/summary")
    public ResponseEntity<TransactionSummary> getTransactionSummary(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {

        TransactionSummary summary = accountService.getTransactionSummary(currentUser, accountNumber);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<AccountResponse>> findAccountsByType(
            @AuthenticationPrincipal User currentUser,
            @RequestParam AccountType type) {

        List<AccountResponse> responses = accountService.findAccountsByType(currentUser, type);
        return ResponseEntity.ok(responses);
    }

    // ===== Solo ADMIN (la validación de rol vive en el service) =====

    @PostMapping("/{accountNumber}/block")
    public ResponseEntity<AccountResponse> blockAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {

        AccountResponse response = accountService.blockAccount(currentUser, accountNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/activate")
    public ResponseEntity<AccountResponse> activateAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {

        AccountResponse response = accountService.activateAccount(currentUser, accountNumber);
        return ResponseEntity.ok(response);
    }
}
