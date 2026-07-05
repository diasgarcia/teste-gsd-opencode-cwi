# Phase 1: Fundação — Research

**Date:** 2026-07-05
**Status:** Complete
**Scope:** INFRA-01, INFRA-02, INFRA-03 — Docker Compose + PostgreSQL, application.properties, Flyway migration V1

---

## 1. Docker Compose — PostgreSQL 16 Best Practices

### 1.1 Minimal Viable `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: partitura-db
    environment:
      POSTGRES_DB: partitura
      POSTGRES_USER: partitura
      POSTGRES_PASSWORD: partitura
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U partitura -d partitura"]
      interval: 5s
      timeout: 3s
      retries: 5
      start_period: 10s
    restart: unless-stopped

volumes:
  pgdata:
```

### 1.2 Key Decisions & Rationale

| Aspect | Choice | Why |
|--------|--------|-----|
| **Image** | `postgres:16-alpine` | Alpine é ~50% menor que a imagem full. PostgreSQL 16 é a versão estável mais recente exigida pelo projeto. |
| **Container name** | `partitura-db` | Nome fixo para referência fácil em logs (`docker logs partitura-db`) e redes Docker. |
| **Volume** | Named volume `pgdata:` (sem path host) | Decisão D-02 do CONTEXT.md. Portátil, sem sujar o sistema de ficheiros do host. Reset: `docker compose down -v`. |
| **Port** | `5432:5432` | Standard PostgreSQL. O host recebe 5432 — muda-se só se houver conflito (ex: outro Postgres local). |
| **Healthcheck** | `pg_isready -U partitura -d partitura` | Essencial para orquestração e para o Spring Boot saber quando o banco está disponível (via `spring.datasource.hikari.initialization-fail-timeout`). |
| **Restart** | `unless-stopped` | Volta automaticamente após reboot da máquina sem atrapalhar debug. |

### 1.3 Gotchas

- **Windows + Docker Desktop:** O volume nomeado é gerido pelo Docker, não há bind mount no filesystem. Isto é bom — evita problemas de permissão NTFS vs. Linux. O volume vive dentro da VM do Docker Desktop.
- **`POSTGRES_DB` vs. `POSTGRES_USER`:** Se ambos forem `partitura`, o banco e o user têm o mesmo nome. Funciona perfeitamente. A imagem PostgreSQL cria ambos.
- **`pg_isready` no healthcheck:** O `-U` e `-d` são obrigatórios para que o comando teste contra a base correcta (senão testa contra `postgres` default, que pode estar OK mesmo que `partitura` não exista ainda).
- **Alpine + timezone:** A imagem Alpine não tem timezone configurado. O PostgreSQL usa UTC por default. Se a app precisar de outro fuso, configurar via `TZ` env var ou no `postgresql.conf`. Para esta fase, UTC é aceitável.
- **`restart: unless-stopped` vs Docker Compose V2:** Em Docker Compose V2 (plugin `docker compose`), o comportamento é idêntico ao V1. Em `docker-compose` (standalone), pode diferir. O projeto deve usar `docker compose` (V2).

---

## 2. Spring Boot 4.1.0 — Property Configuration for PostgreSQL + Flyway

### 2.1 Atenção: Spring Boot 4.1.0

Spring Boot **4.1.0** é a versão mais recente (lançada em 2025). Mudanças relevantes face ao 3.x:

| Mudança | Impacto |
|---------|---------|
| **`spring-boot-starter-webmvc`** substitui `spring-boot-starter-web` | O pom.xml já usa `spring-boot-starter-webmvc` — correcto para 4.x. |
| **`spring-boot-starter-flyway`** incluí automaticamente `flyway-database-postgresql`? | NO — o pom.xml tem a dependência `flyway-database-postgresql` separada. Isto é **obrigatório** para Flyway + PostgreSQL em qualquer versão do Spring Boot. O starter não inclui o driver específico. |
| **`spring.flyway.*`** properties mantêm-se | API estável — as propriedades são as mesmas desde Spring Boot 2.x. Nenhuma breaking change relevante. |
| **Java 17** é o mínimo | O pom.xml define `<java.version>17</java.version>` — correcto. Spring Boot 4.x requer Java 17+. |

### 2.2 Propriedades Exatas para `application.properties`

```properties
# ============================================================
# Application
# ============================================================
spring.application.name=partitura-api

# ============================================================
# PostgreSQL Datasource
# ============================================================
spring.datasource.url=jdbc:postgresql://localhost:5432/partitura
spring.datasource.username=partitura
spring.datasource.password=partitura
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection pool (HikariCP — default do Spring Boot)
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000

# ============================================================
# JPA / Hibernate
# ============================================================
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# ============================================================
# Flyway
# ============================================================
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false

# ============================================================
# Logging
# ============================================================
logging.level.br.com.rocket.partitura=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.flywaydb=DEBUG
logging.level.com.zaxxer.hikari=INFO
```

### 2.3 Explicação Property a Property

| Property | Valor | Porquê |
|----------|-------|--------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/partitura` | Standard JDBC URL. `localhost` porque o Docker expõe na porta 5432 do host. |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` | Pode ser omitido (Spring Boot deteta automagicamente), mas explícito é mais claro. |
| `spring.jpa.database-platform` | `org.hibernate.dialect.PostgreSQLDialect` | Hibernate 6+ usa `PostgreSQLDialect` (sem `InnoDB` ou versão numérica). O Spring Boot 4.x com Hibernate 6.x deteta automaticamente, mas explícito evita ambiguidade em CI. |
| `spring.jpa.hibernate.ddl-auto=validate` | validate | Decisão D-04 do CONTEXT.md. Flyway gere o schema; Hibernate só valida. |
| `spring.jpa.show-sql=true` | true | Decisão D-03. Útil em dev para ver queries geradas. |
| `spring.jpa.open-in-view=false` | false | **IMPORTANTE:** OSIV (Open Session In View) é anti-pattern. Desligar explícito para evitar lazy-loading silencioso em controllers. |
| `spring.flyway.baseline-on-migrate` | false | Não fazer baseline — a base está vazia, a V1 é a primeira migration. `true` só seria necessário se já houvesse tabelas não geridas por Flyway. |
| `logging.level.br.com.rocket.partitura=DEBUG` | DEBUG | Decisão D-03. Logs da aplicação em DEBUG. |
| `logging.level.org.hibernate.SQL=DEBUG` | DEBUG | Mostra as queries SQL reais (com parâmetros — isso é `TRACE`). |
| `logging.level.org.flywaydb=DEBUG` | DEBUG | Mostra cada migration a ser executada. |

### 2.4 Gotchas

- **Spring Boot 4.x + `spring.jpa.open-in-view`:** Desde Spring Boot 2.x que o default é `true` (OSIV). Em 4.x continua a ser `true`. **Desligar explicitamente** é boa prática para evitar `LazyInitializationException` silenciosas.
- **`spring.jpa.show-sql` vs. `logging.level.org.hibernate.SQL`:** `show-sql=true` escreve para stdout directo (não passa pelo logger). `logging.level.org.hibernate.SQL=DEBUG` passa pelo logger e pode ser formatado/redireccionado. É comum usar ambos.
- **HikariCP connection timeout vs. Flyway timeout:** Se o PostgreSQL ainda não estiver pronto quando a app arranca, o Flyway falha antes do Hikari timeout. Solução: Docker healthcheck + `spring.datasource.hikari.initialization-fail-timeout=-1` (opcional, se quiser que a app espere).
- **`spring-boot-starter-webmvc` (não `spring-boot-starter-web`):** O pom.xml usa `spring-boot-starter-webmvc`, que é a versão 4.x. Se alguém tentar adicionar `spring-boot-starter-web` (3.x), causa conflito de versões. Manter apenas `webmvc`.

---

## 3. Flyway Migration V1 — Naming + DDL para PostgreSQL

### 3.1 Naming Convention

```
db/migration/
  V1__criar_tabela_partitura.sql
```

Regras:

| Regra | Explicação |
|-------|------------|
| **Prefixo `V`** | Versão (versioned migration). Maiúsculo obrigatório. |
| **Número da versão** | `1`, `2`, ... ou `1.1`, `1.2`, ... (underscores viram pontos). Para este projecto, números inteiros (`V1`, `V2`, ...) são suficientes. |
| **Separador** | `__` (double underscore). **Nunca** usar single underscore — Flyway usa o primeiro `__` para separar versão de descrição. |
| **Descrição** | `criar_tabela_partitura` — snake_case, em português, descritiva. |
| **Extensão** | `.sql` |

### 3.2 DDL Completo

```sql
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
```

### 3.3 Schema Details — Campo a Campo

| Coluna | Tipo | Constraints | Notas Técnicas |
|--------|------|-------------|----------------|
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Auto-incremento PostgreSQL. `BIGSERIAL` = `BIGINT` + sequence. |
| `titulo` | `VARCHAR(255)` | `NOT NULL` | Tamanho razoável para títulos. |
| `compositor` | `VARCHAR(255)` | `NOT NULL` | `255` cobre nomes longos. |
| `instrumento` | `VARCHAR(255)` | `NOT NULL` | Ex: "Piano", "Violino", "Guitarra". |
| `genero` | `VARCHAR(100)` | opcional | Campo extra do `brief-partitura-api.md` que não estava no schema do CONTEXT.md — **incluir na migration**. |
| `dificuldade` | `VARCHAR(50)` | `NOT NULL` + `CHECK` | CHECK constraint garante integridade ao nível do banco, mirror do enum Java `Dificuldade`. |
| `ano` | `INTEGER` | opcional | Campo extra — **incluir na migration**. Sem CHECK de range (pode ser século XVIII). |
| `arquivo_url` | `VARCHAR(500)` | opcional | `500` para URLs longos com query params. |
| `observacoes` | `TEXT` | opcional | Sem limite de tamanho. |
| `criado_em` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Sem fuso horário explícito. Usar `TIMESTAMP` (sem time zone) — o PostgreSQL assume o timezone da sessão. Alternativa: `TIMESTAMPTZ`. Decisão: usar `TIMESTAMP` para simplicidade em dev. |
| `atualizado_em` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | O trigger de actualização em cada UPDATE será implementado na Phase 2 (entidade JPA com `@PreUpdate`). |

### 3.4 Índices Recomendados (não incluídos na V1)

Para a v1, apenas a PK é suficiente. Para fases futuras, considerar índices opcionais:

```sql
-- Phase 2+ (não incluir na V1)
CREATE INDEX idx_partituras_titulo      ON partituras (titulo);
CREATE INDEX idx_partituras_compositor  ON partituras (compositor);
CREATE INDEX idx_partituras_instrumento ON partituras (instrumento);
```

### 3.5 Idempotência e Resiliência

| Técnica | Aplicável? | Notas |
|---------|-----------|-------|
| `IF NOT EXISTS` | Não | Flyway rastreia migrations no `flyway_schema_history`. Se a V1 já correu, não corre outra vez. `IF NOT EXISTS` seria redundante. |
| `DROP TABLE IF EXISTS ... CASCADE` | Não recomendado | Destrutivo em produção. Para dev, `docker compose down -v` + `up` é mais seguro que `DROP TABLE` em migration. |
| `-- migrate:rollback` | Opcional | Flyway suporta undo migrations (prefiso `U1__`), mas para v1 não há nada a reverter. |
| Repeatable migrations (`R__`) | Para views/functions | Não aplicável nesta fase. |

### 3.6 `COMMENT ON` — Boa Prática

Os `COMMENT ON TABLE/COLUMN` são documentação inline no schema. PostgreSQL armazena-os e ferramentas como `psql \d+`, DBeaver, DataGrip exibem-nos automaticamente. É leve e gratuito — incluir sempre.

---

## 4. Gotchas e Compatibilidade entre Versões

### 4.1 Spring Boot 4.1.0 + Jakarta EE 11

Spring Boot 4.x migrou para Jakarta EE 11 (vs. Jakarta EE 10 no Boot 3.x). Isto afecta:

- `javax.*` → `jakarta.*` — já tratado pelo Spring Initializr
- `spring-boot-starter-webmvc` — nome correcto para Boot 4.x (não `spring-boot-starter-web`)

**Verificação:** O pom.xml usa `spring-boot-starter-webmvc`. **Confirmado correcto.**

### 4.2 Flyway + PostgreSQL Driver Compatibility

| Componente | Versão no pom.xml | Compatível? |
|------------|-------------------|-------------|
| Spring Boot 4.1.0 | parent | Flyway 10.x (incluído no starter) |
| flyway-database-postgresql | gerido pelo starter | Sim — necessário separado do core |
| PostgreSQL JDBC Driver | gerido pelo starter (42.x) | Sim — compatível com PostgreSQL 16 |

### 4.3 Hibernate 6.x + PostgreSQLDialect

O `PostgreSQLDialect` no Hibernate 6.x:
- É auto-detectado (Spring Boot detecta PostgreSQL no URL)
- Não precisa de versão numérica (`PostgreSQL95Dialect`, `PostgreSQL10Dialect` estão deprecados)
- `PostgreSQLDialect` (sem package) é o novo nome canónico desde Hibernate 6

### 4.4 `TIMESTAMP` vs `TIMESTAMPTZ`

| Tipo | Comportamento |
|------|---------------|
| `TIMESTAMP` (sem TZ) | Armazena como está. Se o app passa `2026-07-05T19:40:00`, guarda esse valor exacto, sem conversão de fuso. |
| `TIMESTAMPTZ` (com TZ) | Converte para UTC internamente. Ao ler, converte para o fuso da sessão. |

Para esta fase, usar `TIMESTAMP` é suficiente. Se mais tarde houver requisitos de multi-fuso, migrar com `V2__migrar_timestamptz`.

### 4.5 `CURRENT_TIMESTAMP` no DEFAULT

`CURRENT_TIMESTAMP` em PostgreSQL retorna o timestamp do início da transacção (não o momento real de inserção). Isto é standard SQL e consistente. Se quiser o momento exacto do INSERT, use `clock_timestamp()`, mas isso quebra replicação. Manter `CURRENT_TIMESTAMP`.

### 4.6 Porta 5432 — Conflitos

- Se o dev já tiver PostgreSQL nativo a correr em 5432, alterar a porta no `docker-compose.yml` para `5433:5432` e actualizar `spring.datasource.url`.
- **Decisão:** Manter 5432 no docker-compose. Se houver conflito, o dev ajusta localmente (não comitar).

### 4.7 Resumo de Decisões Pendentes

| Questão | Decisão | Responsável |
|---------|---------|-------------|
| Nome da tabela: `partitura` (singular) ou `partituras` (plural)? | **`partituras`** — plural é convenção SQL. O CONTEXT.md diz "partitura table" mas o REQUIREMENTS.md diz "tabela partituras". A migration criará `partituras`. | PLAN deve confirmar |
| Campos `genero` e `ano` — estão no `brief-partitura-api.md` mas não no CONTEXT.md. Incluir na V1? | **Sim** — são opcionais e fazem parte da entidade. Incluir na migration evita uma V2 só para adicionar colunas nullable. | PLAN deve documentar |
| Trigger de `atualizado_em`? | **Não na V1** — a entidade JPA (Phase 2) usará `@PreUpdate` para actualizar o campo. Trigger SQL seria redundante e menos portável. | Documentar no PLAN |

### 4.8 Ordem de Implementação Recomendada

1. **docker-compose.yml** — primeiro, para o banco estar disponível durante o desenvolvimento da migration
2. **V1__criar_tabela_partitura.sql** — segundo, para poder testar a migration contra o banco real
3. **application.properties** — último, pois depende de ambas para ser testado ponta-a-ponta

---

## 5. Checklist de Verificação (para o PLAN e para a execução)

### INFRA-01 — docker-compose.yml
- [ ] Imagem `postgres:16-alpine`
- [ ] Container name `partitura-db`
- [ ] ENV: `POSTGRES_DB=partitura`, `POSTGRES_USER=partitura`, `POSTGRES_PASSWORD=partitura`
- [ ] Port `5432:5432`
- [ ] Volume named `pgdata:/var/lib/postgresql/data`
- [ ] Healthcheck com `pg_isready -U partitura -d partitura`
- [ ] `restart: unless-stopped`

### INFRA-02 — Flyway Migration V1
- [ ] Ficheiro em `src/main/resources/db/migration/V1__criar_tabela_partitura.sql`
- [ ] `CREATE TABLE partituras`
- [ ] Todas as colunas: id, titulo, compositor, instrumento, genero, dificuldade, ano, arquivo_url, observacoes, criado_em, atualizado_em
- [ ] `CHECK` constraint em dificuldade com os 3 valores
- [ ] `COMMENT ON` para tabela e colunas
- [ ] Sem `IF NOT EXISTS` (Flyway controla execução)

### INFRA-03 — application.properties
- [ ] `spring.datasource.url`, `username`, `password`
- [ ] `spring.jpa.hibernate.ddl-auto=validate`
- [ ] `spring.jpa.show-sql=true`
- [ ] `spring.jpa.open-in-view=false`
- [ ] `spring.flyway.enabled=true`
- [ ] Logging: INFO para Spring, DEBUG para SQL e Flyway

---

*Research prepared: 2026-07-05*
*Next step: PLAN.md for Phase 1*
