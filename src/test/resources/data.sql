
-- =========================================================================
-- 2. INSERÇÃO DO USUÁRIO DE TESTE (Exigido pelos testes de integração)
-- =========================================================================
-- UUID textual fixo que o OrderFlowIntegrationTest está buscando no log
INSERT INTO tb_user_account (id, username, password_hash, role, created_at)
VALUES (
    UUID_TO_BIN('36e174d0-24f3-4e17-a603-61dbcc20a68b'), 
    'user', 
    '$2a$10$xyzDonutPasswordHashHereForSecurityDontChange', 
    'ROLE_USER', 
    NOW()
);

-- Usuário extra genérico caso algum teste busque apenas por 'user' com outro UUID gerado dinamicamente
INSERT INTO tb_user_account (id, username, password_hash, role, created_at)
VALUES (
    UUID_TO_BIN('11111111-2222-3333-4444-555555555555'), 
    'admin', 
    '$2a$10$xyzDonutPasswordHashHereForSecurityDontChange', 
    'ROLE_ADMIN', 
    NOW()
)
ON DUPLICATE KEY UPDATE username=username;


-- =========================================================================
-- 3. INSERÇÃO DE PRODUTOS (Para testes de estoque, rollback e totais)
-- =========================================================================
-- Produto 1: Estoque Saudável (Para sucesso nos pedidos)
INSERT INTO tb_product (id, name, description, price, category, stock_quantity, created_at, updated_at)
VALUES (
    UUID_TO_BIN('a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6'),
    'Teclado Mecânico Gamer',
    'Teclado RGB Switch Blue',
    350.0000,
    'Eletrônicos',
    50,
    NOW(),
    NOW()
);

-- Produto 2: Estoque Zerado/Baixo (Para forçar o erro de "shouldRollbackWhenStockIsInsufficient")
INSERT INTO tb_product (id, name, description, price, category, stock_quantity, created_at, updated_at)
VALUES (
    UUID_TO_BIN('f1f2f3f4-e1e2-d1d2-c1c2-b1b2b3b4b5b6'),
    'Mouse Pad Simples',
    'Mouse pad preto básico',
    15.0000,
    'Acessórios',
    0, 
    NOW(),
    NOW()
);


-- =========================================================================
-- 4. INSERÇÃO DE PEDIDOS PRÉ-EXISTENTES (Para testes de idempotência e fluxos de pagamento)
-- =========================================================================
-- Pedido 1: Status PENDENTE (Para testar fluxo de pagamento e transição de status bem-sucedida)
INSERT INTO tb_orders (id, user_account_id, status, total, created_at, updated_at)
VALUES (
    UUID_TO_BIN('99999999-8888-7777-6666-555555555555'),
    UUID_TO_BIN('36e174d0-24f3-4e17-a603-61dbcc20a68b'),
    'PENDENTE',
    350.0000,
    NOW(),
    NOW()
);

INSERT INTO tb_order_item (id, order_id, product_id, product_name_snapshot, unit_price_snapshot, quantity, subtotal)
VALUES (
    UUID_TO_BIN('88888888-7777-6666-5555-444444444444'),
    UUID_TO_BIN('99999999-8888-7777-6666-555555555555'),
    UUID_TO_BIN('a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6'),
    'Teclado Mecânico Gamer',
    350.0000,
    1,
    350.0000
);

-- Pedido 2: Status PAGO (Para testar o erro de "shouldNotAllowPayingAnAlreadyPaidOrder" e "shouldNotUpdateStockTwice")
INSERT INTO tb_orders (id, user_account_id, status, total, created_at, updated_at)
VALUES (
    UUID_TO_BIN('77777777-6666-5555-4444-333333333333'),
    UUID_TO_BIN('36e174d0-24f3-4e17-a603-61dbcc20a68b'),
    'PAGO',
    700.0000,
    NOW(),
    NOW()
);

INSERT INTO tb_order_item (id, order_id, product_id, product_name_snapshot, unit_price_snapshot, quantity, subtotal)
VALUES (
    UUID_TO_BIN('66666666-5555-4444-3333-222222222222'),
    UUID_TO_BIN('77777777-6666-5555-4444-333333333333'),
    UUID_TO_BIN('a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6'),
    'Teclado Mecânico Gamer',
    350.0000,
    2,
    700.0000
);