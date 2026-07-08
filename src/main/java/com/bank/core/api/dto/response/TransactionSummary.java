package com.bank.core.api.dto.response;

import java.math.BigDecimal;

public class TransactionSummary {

    private long totalDeposits;
    private long totalWithdrawals;
    private long totalTransfers;
    private BigDecimal totalDepositAmount;
    private BigDecimal totalWithdrawalAmount;
    private BigDecimal totalTransferAmount;
    private BigDecimal netBalanceChange;

    // Constructor vacío
    public TransactionSummary() {
        this.totalDepositAmount = BigDecimal.ZERO;
        this.totalWithdrawalAmount = BigDecimal.ZERO;
        this.totalTransferAmount = BigDecimal.ZERO;
        this.netBalanceChange = BigDecimal.ZERO;
    }

    // Getters y Setters
    public long getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(long totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public long getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(long totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public long getTotalTransfers() {
        return totalTransfers;
    }

    public void setTotalTransfers(long totalTransfers) {
        this.totalTransfers = totalTransfers;
    }

    public BigDecimal getTotalDepositAmount() {
        return totalDepositAmount;
    }

    public void setTotalDepositAmount(BigDecimal totalDepositAmount) {
        this.totalDepositAmount = totalDepositAmount;
    }

    public BigDecimal getTotalWithdrawalAmount() {
        return totalWithdrawalAmount;
    }

    public void setTotalWithdrawalAmount(BigDecimal totalWithdrawalAmount) {
        this.totalWithdrawalAmount = totalWithdrawalAmount;
    }

    public BigDecimal getTotalTransferAmount() {
        return totalTransferAmount;
    }

    public void setTotalTransferAmount(BigDecimal totalTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
    }

    public BigDecimal getNetBalanceChange() {
        return netBalanceChange;
    }

    public void setNetBalanceChange(BigDecimal netBalanceChange) {
        this.netBalanceChange = netBalanceChange;
    }
}