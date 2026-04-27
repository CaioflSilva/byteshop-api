CREATE TABLE pedidos (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT         NOT NULL,
    status        VARCHAR(30)    NOT NULL DEFAULT 'PENDENTE',
    total         NUMERIC(19, 2) NOT NULL,
    criado_em     TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE itens_pedido (
    id          BIGSERIAL PRIMARY KEY,
    pedido_id   BIGINT         NOT NULL,
    produto_id  BIGINT         NOT NULL,
    quantidade  INTEGER        NOT NULL,
    preco_unit  NUMERIC(19, 2) NOT NULL,
    subtotal    NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_item_pedido   FOREIGN KEY (pedido_id)  REFERENCES pedidos(id),
    CONSTRAINT fk_item_produto2 FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE INDEX idx_pedido_usuario ON pedidos(usuario_id);
CREATE INDEX idx_pedido_status  ON pedidos(status);