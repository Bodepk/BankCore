package com.bank.core.domain.model;

import com.bank.core.domain.enums.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una transacción bancaria.
 */
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    /**
     * ID único de la transacción (formato: TX-YYYYMMDD-XXXXX)
     */
    @Column(name = "transaction_id", unique = true, nullable = false, length = 50)
    private String transactionId;

    /**
     * Cuenta origen (para retiros y transferencias)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    /**
     * Cuenta destino (para depósitos y transferencias)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    /**
     * Tipo de transacción
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    /**
     * Monto de la transacción
     */
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Saldo antes de la transacción
     */
    @Column(name = "balance_before", precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    /**
     * Saldo después de la transacción
     */
    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    /**
     * Descripción de la transacción
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Referencia externa (número de factura, etc.)
     */
    @Column(name = "reference", length = 50)
    private String reference;

    /**
     * Fecha de la transacción
     */
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {
        super();
        this.transactionDate = LocalDateTime.now();
    }

    // ===== GETTERS Y SETTERS =====

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}