package com.bank.core.domain.model;

import com.bank.core.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una transacción bancaria.
 */
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(name = "transaction_id", unique = true, nullable = false, length = 50)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "reference", length = 50)
    private String reference;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {
        super();
        this.transactionDate = LocalDateTime.now();
        this.transactionId = generateTransactionId();
    }

    /**
     * Genera un ID único para la transacción.
     * Formato: TX-YYYYMMDD-XXXXX (ej: TX-20260705-00001)
     */
    private String generateTransactionId() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%05d", (int) (Math.random() * 100000));
        return "TX-" + date + "-" + random;
    }

    //GETTERS Y SETTERS

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId);
    }
}