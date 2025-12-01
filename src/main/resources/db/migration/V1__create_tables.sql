CREATE TABLE users (
    id BINARY(16) NOT NULL,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_login UNIQUE (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE cards (
    id BINARY(16) NOT NULL,
    pan TEXT NOT NULL,
    pan_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BINARY(16) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_card_pan_hash UNIQUE (pan_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX idx_card_pan_hash ON cards(pan_hash);

INSERT INTO users (id, login, password)
VALUES (
    UUID_TO_BIN(UUID()),
    'admin',
    '$2a$10$W3CNKvUWipBeZouwkX5CMekmXIlDybCUNBrxK8/nghT0O444VKDGy'
);