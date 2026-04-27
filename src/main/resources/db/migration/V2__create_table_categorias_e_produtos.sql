CREATE TABLE categorias (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL UNIQUE,
    descricao   VARCHAR(255),
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE produtos (
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(200) NOT NULL,
    descricao    TEXT,
    preco        NUMERIC(19, 2) NOT NULL,
    estoque      INTEGER        NOT NULL DEFAULT 0,
    ativo        BOOLEAN        NOT NULL DEFAULT TRUE,
    categoria_id BIGINT         NOT NULL,
    criado_em    TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE INDEX idx_produto_categoria ON produtos(categoria_id);
CREATE INDEX idx_produto_ativo ON produtos(ativo);
CREATE INDEX idx_produto_preco ON produtos(preco);