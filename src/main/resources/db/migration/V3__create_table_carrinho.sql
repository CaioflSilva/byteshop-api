CREATE TABLE carrinhos (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT    NOT NULL UNIQUE,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_carrinho_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE itens_carrinho (
    id          BIGSERIAL PRIMARY KEY,
    carrinho_id BIGINT         NOT NULL,
    produto_id  BIGINT         NOT NULL,
    quantidade  INTEGER        NOT NULL DEFAULT 1,
    preco_unit  NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_item_carrinho FOREIGN KEY (carrinho_id) REFERENCES carrinhos(id),
    CONSTRAINT fk_item_produto  FOREIGN KEY (produto_id)  REFERENCES produtos(id),
    CONSTRAINT uq_carrinho_produto UNIQUE (carrinho_id, produto_id)
);