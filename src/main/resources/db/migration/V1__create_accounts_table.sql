-- =============================================
-- MIGRACIÓN V1: CREAR TABLA DE CUENTAS
-- =============================================

-- Crear la tabla accounts
CREATE TABLE IF NOT EXISTS accounts (
    -- ID primario (UUID generado automáticamente)
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Número de cuenta (único)
    account_number VARCHAR(20) NOT NULL UNIQUE,

    -- Tipo de cuenta (SAVINGS, CHECKING, BUSINESS)
    account_type VARCHAR(20) NOT NULL,

    -- Saldo actual (2 decimales)
    balance DECIMAL(19, 2) DEFAULT 0.00,

    -- Moneda (USD, EUR, etc.)
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',

    -- Estado de la cuenta
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Límite de descubierto
    overdraft_limit DECIMAL(19, 2) DEFAULT 0.00,

    -- Tasa de interés (para cuentas de ahorro)
    interest_rate DECIMAL(5, 2) DEFAULT 0.00,

    -- Campos de auditoría (heredados de BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones
    CONSTRAINT check_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT check_currency_length CHECK (LENGTH(currency) = 3)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_account_type ON accounts(account_type);
CREATE INDEX idx_accounts_created_at ON accounts(created_at);

-- =============================================
-- COMENTARIOS PARA DOCUMENTACIÓN
-- =============================================
COMMENT ON TABLE accounts IS 'Tabla de cuentas bancarias';
COMMENT ON COLUMN accounts.id IS 'ID único de la cuenta (UUID)';
COMMENT ON COLUMN accounts.account_number IS 'Número de cuenta (único)';
COMMENT ON COLUMN accounts.account_type IS 'Tipo de cuenta: SAVINGS, CHECKING, BUSINESS';
COMMENT ON COLUMN accounts.balance IS 'Saldo actual de la cuenta';
COMMENT ON COLUMN accounts.currency IS 'Moneda de la cuenta (código ISO 4217)';
COMMENT ON COLUMN accounts.status IS 'Estado: ACTIVE, INACTIVE, BLOCKED, CLOSED';
COMMENT ON COLUMN accounts.overdraft_limit IS 'Límite de descubierto permitido';
COMMENT ON COLUMN accounts.interest_rate IS 'Tasa de interés anual (%)';