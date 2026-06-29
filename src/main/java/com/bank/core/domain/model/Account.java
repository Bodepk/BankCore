package com.bank.core.domain.model;

import com.bank.core.domain.enums.AccountStatus;
import com.bank.core.domain.enums.AccountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa una cuenta bancaria.
 *
 * @Entity: Indica que esta clase es una entidad JPA
 * @Table: Define el nombre de la tabla en la base de datos
 */
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    /**
     * Número de cuenta (único).
     * - @Column(unique = true): Asegura que no haya duplicados
     * - nullable = false: No puede ser nulo
     * - length = 20: Tamaño máximo
     */
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    /**
     * Tipo de cuenta (Ahorro, Corriente, Empresarial).
     * - @Enumerated(EnumType.STRING): Guarda el nombre del enum en la BD
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    /**
     * Balance actual de la cuenta.
     * - @Column(precision = 19, scale = 2): 19 dígitos, 2 decimales
     * - columnDefinition = "DECIMAL(19,2) DEFAULT 0.00": Valor por defecto
     */
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Moneda de la cuenta (USD, EUR, etc.)
     */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    /**
     * Estado de la cuenta (Activa, Bloqueada, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    /**
     * Límite de descubierto (si es negativo)
     */
    @Column(name = "overdraft_limit", precision = 19, scale = 2)
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    /**
     * Tasa de interés (para cuentas de ahorro)
     */
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = BigDecimal.ZERO;

    /**
     * Constructor vacío (requerido por JPA)
     */
    public Account() {
        super();
    }

    /**
     * Constructor con parámetros para crear cuentas
     */
    public Account(String accountNumber, AccountType accountType, String currency) {
        super();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.overdraftLimit = BigDecimal.ZERO;
        this.interestRate = BigDecimal.ZERO;
    }

    // ===== MÉTODOS DE NEGOCIO =====

    /**
     * Deposita dinero en la cuenta.
     *
     * @param amount Cantidad a depositar (debe ser positiva)
     * @throws IllegalArgumentException si el monto es negativo o cero
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Retira dinero de la cuenta.
     *
     * @param amount Cantidad a retirar (debe ser positiva)
     * @throws IllegalArgumentException si el monto es negativo o cero
     * @throws IllegalStateException si la cuenta no está activa
     * @throws IllegalStateException si el saldo es insuficiente (incluyendo descubierto)
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("La cuenta no está activa");
        }

        // Verificar saldo suficiente (incluyendo límite de descubierto)
        BigDecimal availableBalance = this.balance.add(this.overdraftLimit);
        if (amount.compareTo(availableBalance) > 0) {
            throw new IllegalStateException(
                    String.format("Saldo insuficiente. Disponible: %s, Solicitado: %s",
                            availableBalance, amount)
            );
        }

        this.balance = this.balance.subtract(amount);
    }

    /**
     * Aplica interés a la cuenta (para cuentas de ahorro).
     */
    public void applyInterest() {
        if (this.accountType == AccountType.SAVINGS &&
                this.interestRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interest = this.balance.multiply(this.interestRate)
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            this.balance = this.balance.add(interest);
        }
    }

    /**
     * Verifica si la cuenta tiene suficiente saldo para una operación.
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        BigDecimal availableBalance = this.balance.add(this.overdraftLimit);
        return amount.compareTo(availableBalance) <= 0;
    }

    // ===== GETTERS Y SETTERS =====

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", overdraftLimit=" + overdraftLimit +
                '}';
    }
}