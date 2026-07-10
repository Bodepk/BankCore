-- =============================================
-- MIGRACIÓN V4: CREAR TABLA DE USUARIOS
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    account_locked BOOLEAN DEFAULT FALSE,
    full_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Comentarios
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema';
COMMENT ON COLUMN users.username IS 'Nombre de usuario único';
COMMENT ON COLUMN users.email IS 'Email único para login';
COMMENT ON COLUMN users.password IS 'Contraseña encriptada (BCrypt)';
COMMENT ON COLUMN users.role IS 'Rol: ADMIN o USER';