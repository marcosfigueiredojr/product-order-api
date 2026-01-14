CREATE TABLE tb_order_item (
    id BINARY(16) NOT NULL,
    order_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    product_name_snapshot VARCHAR(255) NOT NULL,
    unit_price_snapshot DECIMAL(19,4) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(19,4) NOT NULL,

    CONSTRAINT pk_tb_order_item PRIMARY KEY (id),
    CONSTRAINT fk_tb_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES tb_orders (id),
    CONSTRAINT fk_tb_order_item_product
        FOREIGN KEY (product_id)
        REFERENCES tb_product (id)
) ENGINE=InnoDB;

CREATE INDEX idx_tb_order_item_order ON tb_order_item (order_id);
CREATE INDEX idx_tb_order_item_product ON tb_order_item (product_id);
