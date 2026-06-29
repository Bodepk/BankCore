package com.bank.core.domain.enums;

/**
 * Tipos de transacciones bancarias.
 */
public enum TransactionType {

    DEPOSIT("Depósito", "Ingreso de dinero"),
    WITHDRAWAL("Retiro", "Extracción de dinero"),
    TRANSFER("Transferencia", "Envío a otra cuenta"),
    INTEREST("Interés", "Abono de intereses"),
    FEE("Comisión", "Cobro de comisión"),
    REFUND("Reembolso", "Devolución de dinero");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}