package com.bank.core.api.controller;

import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.Account;
import com.bank.core.service.core.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para operaciones con cuentas bancarias.
 *
 * @RestController: Indica que esta clase es un controlador REST
 * @RequestMapping("/api/v1/accounts"): Base de todas las rutas
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructor con inyección de dependencias.
     * Spring inyecta automáticamente el AccountService.
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Crea una nueva cuenta bancaria.
     *
     * POST /api/v1/accounts?accountNumber=123&accountType=SAVINGS&currency=USD
     *
     * @param accountNumber Número de cuenta (único)
     * @param accountType Tipo de cuenta (SAVINGS, CHECKING, BUSINESS)
     * @param currency Moneda (USD por defecto)
     * @return Cuenta creada con estado 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestParam String accountNumber,
            @RequestParam AccountType accountType,
            @RequestParam(defaultValue = "USD") String currency) {

        Account account = accountService.createAccount(accountNumber, accountType, currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * Busca una cuenta por su número.
     *
     * GET /api/v1/accounts/1234567890
     *
     * @param accountNumber Número de cuenta
     * @return Cuenta encontrada
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    /**
     * Lista todas las cuentas activas.
     *
     * GET /api/v1/accounts
     *
     * @return Lista de cuentas activas
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllActiveAccounts() {
        List<Account> accounts = accountService.findAllActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Deposita dinero en una cuenta.
     *
     * POST /api/v1/accounts/1234567890/deposit?amount=1000
     *
     * @param accountNumber Número de cuenta
     * @param amount Monto a depositar
     * @return Cuenta actualizada
     */
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {

        Account account = accountService.deposit(accountNumber, amount);
        return ResponseEntity.ok(account);
    }

    /**
     * Retira dinero de una cuenta.
     *
     * POST /api/v1/accounts/1234567890/withdraw?amount=500
     *
     * @param accountNumber Número de cuenta
     * @param amount Monto a retirar
     * @return Cuenta actualizada
     */
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {

        Account account = accountService.withdraw(accountNumber, amount);
        return ResponseEntity.ok(account);
    }

    /**
     * Transfiere dinero entre dos cuentas.
     *
     * POST /api/v1/accounts/transfer?sourceAccount=123&destinationAccount=456&amount=100
     *
     * @param sourceAccount Número de cuenta origen
     * @param destinationAccount Número de cuenta destino
     * @param amount Monto a transferir
     * @return Cuenta origen actualizada
     */
    @PostMapping("/transfer")
    public ResponseEntity<Account> transfer(
            @RequestParam String sourceAccount,
            @RequestParam String destinationAccount,
            @RequestParam BigDecimal amount) {

        Account account = accountService.transfer(sourceAccount, destinationAccount, amount);
        return ResponseEntity.ok(account);
    }
}