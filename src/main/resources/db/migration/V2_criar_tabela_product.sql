CREATE TABLE tb_product (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19,4) NOT NULL,
    category VARCHAR(100) NOT NULL,
    stock_quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_tb_product PRIMARY KEY (id)
);


CREATE INDEX idx_tb_product_category ON tb_product (category);
CREATE INDEX idx_tb_product_price ON tb_product (price)
