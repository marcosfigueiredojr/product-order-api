CREATE TABLE IF NOT EXISTS tb_orders (
    id BINARY(16) NOT NULL,
    user_account_id BINARY(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_tb_orders PRIMARY KEY (id),
    CONSTRAINT fk_tb_orders_user_account
        FOREIGN KEY (user_account_id)
        REFERENCES tb_user_account (id)
) ENGINE=InnoDB;

CREATE INDEX idx_tb_orders_user_account ON tb_orders (user_account_id);
CREATE INDEX idx_tb_orders_status ON tb_orders (status);
CREATE INDEX idx_tb_orders_created_at ON tb_orders (created_at);
