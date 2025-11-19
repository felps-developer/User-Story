-- Create usuario table
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create modulo table
CREATE TABLE modulo (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create modulo_departamento table
CREATE TABLE modulo_departamento (
    id BIGSERIAL PRIMARY KEY,
    modulo_id BIGINT NOT NULL REFERENCES modulo(id) ON DELETE CASCADE,
    departamento VARCHAR(50) NOT NULL,
    CONSTRAINT uk_modulo_departamento UNIQUE (modulo_id, departamento)
);

-- Create modulo_incompativel table
CREATE TABLE modulo_incompativel (
    id BIGSERIAL PRIMARY KEY,
    modulo_id BIGINT NOT NULL REFERENCES modulo(id) ON DELETE CASCADE,
    modulo_incompativel_id BIGINT NOT NULL REFERENCES modulo(id) ON DELETE CASCADE,
    CONSTRAINT uk_modulo_incompativel UNIQUE (modulo_id, modulo_incompativel_id),
    CONSTRAINT chk_self_reference CHECK (modulo_id != modulo_incompativel_id)
);

-- Create solicitacao table
CREATE TABLE solicitacao (
    id BIGSERIAL PRIMARY KEY,
    protocolo VARCHAR(50) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    justificativa TEXT NOT NULL,
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    motivo_negacao TEXT,
    motivo_cancelamento TEXT,
    data_solicitacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP,
    solicitacao_origem_id BIGINT REFERENCES solicitacao(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create solicitacao_modulo table
CREATE TABLE solicitacao_modulo (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL REFERENCES solicitacao(id) ON DELETE CASCADE,
    modulo_id BIGINT NOT NULL REFERENCES modulo(id),
    CONSTRAINT uk_solicitacao_modulo UNIQUE (solicitacao_id, modulo_id)
);

-- Create acesso_usuario_modulo table
CREATE TABLE acesso_usuario_modulo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    modulo_id BIGINT NOT NULL REFERENCES modulo(id),
    solicitacao_id BIGINT NOT NULL REFERENCES solicitacao(id),
    data_inicio TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_usuario_modulo_solicitacao UNIQUE (usuario_id, modulo_id, solicitacao_id)
);

-- Create historico_solicitacao table
CREATE TABLE historico_solicitacao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL REFERENCES solicitacao(id) ON DELETE CASCADE,
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    data_acao TIMESTAMP NOT NULL
);

-- Create indexes
CREATE INDEX idx_solicitacao_usuario ON solicitacao(usuario_id);
CREATE INDEX idx_solicitacao_status ON solicitacao(status);
CREATE INDEX idx_usuario_ativo ON acesso_usuario_modulo(usuario_id, ativo);
CREATE INDEX idx_expiracao_ativo ON acesso_usuario_modulo(data_expiracao, ativo);
CREATE INDEX idx_solicitacao_data ON historico_solicitacao(solicitacao_id, data_acao);

