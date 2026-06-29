package com.bank.core.domain.enums;


    /**
     * Enum que define los tipos de cuenta bancaria.
     * Usamos enum porque es una lista fija de valores.
     */
    public enum AccountType {

        /**
         * Cuenta de Ahorro:
         * - Genera intereses
         * - Sin comisiones por mantenimiento
         * - Límite de retiros mensuales
         */
        SAVINGS("Ahorro", "Genera intereses mensuales"),

        /**
         * Cuenta Corriente:
         * - Mayor liquidez
         * - Comisiones por mantenimiento
         * - Permite descubierto
         */
        CHECKING("Corriente", "Para transacciones diarias"),

        /**
         * Cuenta Empresarial:
         * - Para negocios
         * - Límites más altos
         * - Comisiones especiales
         */
        BUSINESS("Empresarial", "Para cuentas corporativas");

        // Atributos del enum
        private final String displayName;
        private final String description;

        /**
         * Constructor privado (los enums no pueden ser instanciados externamente)
         */
        AccountType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        // Getters (sin setters porque los enums son inmutables)
        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

