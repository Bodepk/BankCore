package com.bank.core.api.dto.request;

import com.bank.core.domain.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

    /**
     * DTO para la solicitud de creación de cuenta.
     * No exponemos la entidad directamente por seguridad.
     */
    public class CreateAccountRequest {

        @NotBlank(message = "El número de cuenta es obligatorio")
        @Size(min = 10, max = 20, message = "El número de cuenta debe tener entre 10 y 20 dígitos")
        @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta debe contener solo dígitos")
        private String accountNumber;

        @NotNull(message = "El tipo de cuenta es obligatorio")
        private AccountType accountType;

        @Pattern(regexp = "^[A-Z]{3}$", message = "El código de moneda debe ser de 3 letras mayúsculas (ej: USD)")
        private String currency = "USD";

        // Constructor por defecto
        public CreateAccountRequest() {
        }

        // Getters y Setters
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

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

