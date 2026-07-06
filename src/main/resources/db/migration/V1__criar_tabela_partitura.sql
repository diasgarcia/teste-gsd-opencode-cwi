-- V1__criar_tabela_partitura.sql
-- ============================================================
-- Creates the `partituras` table for storing sheet music entries.
-- INFRA-02: Initial Flyway migration.
-- ============================================================

CREATE TABLE partituras (
    id              BIGSERIAL       PRIMARY KEY,
    titulo          VARCHAR(255)    NOT NULL,
    compositor      VARCHAR(255)    NOT NULL,
    instrumento     VARCHAR(255)    NOT NULL,
    genero          VARCHAR(100),
    dificuldade     VARCHAR(50)     NOT NULL
                        CONSTRAINT chk_partituras_dificuldade
                        CHECK (dificuldade IN ('INICIANTE', 'INTERMEDIARIO', 'AVANCADO')),
    ano             INTEGER,
    arquivo_url     VARCHAR(500),
    observacoes     TEXT,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  partituras              IS 'Tabela principal de partituras';
COMMENT ON COLUMN partituras.id           IS 'Identificador único gerado automaticamente';
COMMENT ON COLUMN partituras.titulo       IS 'Título da partitura (obrigatório)';
COMMENT ON COLUMN partituras.compositor   IS 'Nome do compositor (obrigatório)';
COMMENT ON COLUMN partituras.instrumento  IS 'Instrumento alvo da partitura (obrigatório)';
COMMENT ON COLUMN partituras.genero       IS 'Género musical (opcional)';
COMMENT ON COLUMN partituras.dificuldade  IS 'Nível de dificuldade: INICIANTE, INTERMEDIARIO, AVANCADO';
COMMENT ON COLUMN partituras.ano          IS 'Ano de composição (opcional)';
COMMENT ON COLUMN partituras.arquivo_url  IS 'URL para ficheiro PDF/MusicXML (opcional)';
COMMENT ON COLUMN partituras.observacoes  IS 'Observações gerais sobre a partitura (opcional)';
COMMENT ON COLUMN partituras.criado_em    IS 'Timestamp de criação (preenchido automaticamente)';
COMMENT ON COLUMN partituras.atualizado_em IS 'Timestamp da última atualização (preenchido automaticamente)';
