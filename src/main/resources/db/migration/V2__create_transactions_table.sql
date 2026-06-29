-- =============================================
-- MIGRACIÓN V2: CREAR TABLA DE TRANSACCIONES
-- =============================================

CREATE TABLE IF NOT EXISTS transactions (
    -- ID primario
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ID de la transacción (formato: TX-YYYYMMDD-XXXXX)
    transaction_id VARCHAR(50) NOT NULL UNIQUE,

    -- Cuenta origen (puede ser NULL para depósitos)
    source_account_id VARCHAR(36),

    -- Cuenta destino (puede ser NULL para retiros)
    destination_account_id VARCHAR(36),

    -- Tipo de transacción
    transaction_type VARCHAR(30) NOT NULL,

    -- Monto de la transacción
    amount DECIMAL(19, 2) NOT NULL,

    -- Saldo antes de la transacción
    balance_before DECIMAL(19, 2),

    -- Saldo después de la transacción
    balance_after DECIMAL(19, 2),

    -- Descripción
    description VARCHAR(255),

    -- Referencia externa
    reference VARCHAR(50),

    -- Fecha de la transacción
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Campos de auditoría
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Llaves foráneas
    CONSTRAINT fk_transactions_source_account
        FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_destination_account
        FOREIGN KEY (destination_account_id) REFERENCES accounts(id),

    -- Restricciones
    CONSTRAINT check_amount_positive CHECK (amount > 0)
);

-- Crear índices
CREATE INDEX idx_transactions_transaction_id ON transactions(transaction_id);
CREATE INDEX idx_transactions_source_account ON transactions(source_account_id);
CREATE INDEX idx_transactions_destination_account ON transactions(destination_account_id);
CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_reference ON transactions(reference);

-- =============================================
-- COMENTARIOS
-- =============================================
COMMENT ON TABLE transactions IS 'Tabla de transacciones bancarias';
COMMENT ON COLUMN transactions.transaction_id IS 'ID único de la transacción';
COMMENT ON COLUMN transactions.transaction_type IS 'Tipo: DEPOSIT, WITHDRAWAL, TRANSFER, etc.';
COMMENT ON COLUMN transactions.amount IS 'Monto de la transacción';
COMMENT ON COLUMN transactions.balance_before IS 'Saldo antes de la transacción';
COMMENT ON COLUMN transactions.balance_after IS 'Saldo después de la transacción';