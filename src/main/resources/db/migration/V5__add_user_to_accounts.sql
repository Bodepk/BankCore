-- =============================================
-- MIGRACIÓN V5: VINCULAR CUENTAS CON USUARIOS
-- =============================================

-- Agregar la columna user_id (nullable primero, para no romper filas existentes)
ALTER TABLE accounts
    ADD COLUMN IF NOT EXISTS user_id VARCHAR(36);

-- Llave foránea hacia users
ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_user
        FOREIGN KEY (user_id) REFERENCES users(id);

-- Índice para búsquedas por dueño
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);

COMMENT ON COLUMN accounts.user_id IS 'Usuario dueño de la cuenta';

-- NOTA: si ya tenías cuentas creadas en tu entorno de desarrollo ANTES de esta
-- migración, van a quedar con user_id = NULL (huérfanas). En dev, lo más simple
-- es borrarlas y volver a crearlas ya con dueño:
--   DELETE FROM transactions;
--   DELETE FROM accounts;
