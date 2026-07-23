-- =============================================
-- MIGRACIÓN V3: CREAR TABLA DE TRANSACCIONES
-- =============================================

-- Crear la tabla solo si no existe
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    source_account_id VARCHAR(36),
    destination_account_id VARCHAR(36),
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    balance_before DECIMAL(19, 2),
    balance_after DECIMAL(19, 2),
    description VARCHAR(255),
    reference VARCHAR(50),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_source
        FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_destination
        FOREIGN KEY (destination_account_id) REFERENCES accounts(id),
    CONSTRAINT check_amount_positive CHECK (amount > 0)
);

-- Crear índices solo si no existen
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_id ON transactions(transaction_id);
CREATE INDEX IF NOT EXISTS idx_transactions_source_account ON transactions(source_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_destination_account ON transactions(destination_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_date ON transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transactions_reference ON transactions(reference);

-- =============================================
-- COMENTARIOS
-- =============================================
COMMENT ON TABLE transactions IS 'Tabla de transacciones bancarias';
COMMENT ON COLUMN transactions.transaction_id IS 'ID único de la transacción';
COMMENT ON COLUMN transactions.transaction_type IS 'Tipo: DEPOSIT, WITHDRAWAL, TRANSFER, etc.';
COMMENT ON COLUMN transactions.amount IS 'Monto de la transacción';
COMMENT ON COLUMN transactions.balance_before IS 'Saldo antes de la transacción';
COMMENT ON COLUMN transactions.balance_after IS 'Saldo después de la transacción';