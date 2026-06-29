package com.bank.core.domain.enums;

/**
 * Estado de una cuenta bancaria.
 * Controla qué operaciones puede realizar.
 */
public enum AccountStatus {

    /**
     * Cuenta activa: puede realizar todas las operaciones
     */
    ACTIVE("Activa", "Operaciones permitidas"),

    /**
     * Cuenta inactiva: no permite operaciones
     * Se usa cuando el cliente no usa la cuenta por mucho tiempo
     */
    INACTIVE("Inactiva", "No permite operaciones"),

    /**
     * Cuenta bloqueada: por seguridad o fraude
     * Solo administradores pueden desbloquear
     */
    BLOCKED("Bloqueada", "Bloqueada por seguridad"),

    /**
     * Cuenta cerrada: definitivamente cerrada
     * No se puede reactivar
     */
    CLOSED("Cerrada", "Cuenta cerrada definitivamente");

    private final String displayName;
    private final String description;

    AccountStatus(String displayName, String description) {
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