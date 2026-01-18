CREATE TABLE IF NOT EXISTS tb_user_account (
    id BINARY(16) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tb_user_account PRIMARY KEY (id),
    CONSTRAINT uk_tb_user_account_username UNIQUE (username)
) ENGINE=InnoDB;
