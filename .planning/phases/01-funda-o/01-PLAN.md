# Phase 1: Fundação — PLAN

**Created:** 2026-07-05
**Status:** Ready for execution
**Goal:** Projeto pronto para desenvolvimento — banco de dados configurado, migration inicial aplicada, docker-compose funcional.
**Requirement IDs:** INFRA-01, INFRA-02, INFRA-03
**Plans:** 3 | **Tasks:** 9

---

## Pending Decisions (Resolvidas neste PLAN)

| Questão | Decisão | Justificação |
|---------|---------|-------------|
| Nome da tabela: `partitura` ou `partituras`? | **`partituras`** (plural) | Convenção SQL para nomes de tabelas no plural. 01-RESEARCH.md secção 4.7 confirma. REQUIREMENTS.md já refere "tabela partituras". |
| Incluir `genero` e `ano` na V1? | **Sim** | Campos opcionais presentes no `brief-partitura-api.md` como parte da entidade. Incluir já na V1 evita uma migration V2 só para adicionar colunas nullable. |
| Trigger SQL para `atualizado_em`? | **Não** | A entidade JPA na Phase 2 usará `@PreUpdate` para actualizar o timestamp. Trigger SQL seria redundante e menos portável. |

---

## 1. Visão Geral da Fase

Esta fase entrega a infraestrutura base para desenvolvimento do projecto Partitura API. Nenhum código Java de negócio é escrito — apenas configuração de infraestrutura e schema de base de dados. O resultado é um ambiente local onde:

- **`docker compose up`** sobe PostgreSQL 16 pronto para a aplicação
- **Flyway** aplica a migration inicial com o schema completo da tabela `partituras`
- **Spring Boot** liga-se ao banco com configuração validada (JPA `validate` + HikariCP + logging adequado)

---

## 2. Task Breakdown

### Plano 1: docker-compose + PostgreSQL (INFRA-01)

Criar o ficheiro `docker-compose.yml` na raiz do projecto com PostgreSQL 16 em contentor Docker.

| # | Task | Descrição | Critério de Aceitação |
|---|------|-----------|----------------------|
| 1.1 | **Criar `docker-compose.yml`** | Escrever ficheiro na raiz com service `postgres` usando imagem `postgres:16-alpine`, container name `partitura-db`, ENV `POSTGRES_DB=partitura`, `POSTGRES_USER=partitura`, `POSTGRES_PASSWORD=partitura`, port `5432:5432`, volume nomeado `pgdata:/var/lib/postgresql/data`, healthcheck `pg_isready`, restart `unless-stopped`. | Ficheiro existe em `./docker-compose.yml` com conteúdo exacto conforme 01-RESEARCH.md secção 1.1. |
| 1.2 | **Criar `.env` (opcional)** | Ficheiro `.env` com `POSTGRES_DB=partitura`, `POSTGRES_USER=partitura`, `POSTGRES_PASSWORD=partitura` para evitar hardcode no docker-compose (decisão: hardcode no yml é aceitável para dev local — `.env` fica como opcional, NÃO criar agora). | *(Skip — usar valores literais no docker-compose.yml)* |
| 1.3 | **Verificar `docker compose up`** | Executar `docker compose up -d` e confirmar que o contentor `partitura-db` fica healthy (`docker ps` mostra `healthy` no status, `docker logs partitura-db` mostra "database system is ready to accept connections"). | PostgreSQL 16 a correr na porta 5432, healthcheck passa. |

### Plano 2: Migration Flyway V1 (INFRA-02)

Criar o ficheiro de migration Flyway com o schema completo da tabela `partituras`.

| # | Task | Descrição | Critério de Aceitação |
|---|------|-----------|----------------------|
| 2.1 | **Criar directório `db/migration/`** | Criar `src/main/resources/db/migration/` se não existir. | Directório existe. |
| 2.2 | **Criar `V1__criar_tabela_partitura.sql`** | Escrever ficheiro com `CREATE TABLE partituras (...)`, todas as 11 colunas (id BIGSERIAL PK, titulo VARCHAR(255) NOT NULL, compositor VARCHAR(255) NOT NULL, instrumento VARCHAR(255) NOT NULL, genero VARCHAR(100), dificuldade VARCHAR(50) NOT NULL + CHECK com INICIANTE/INTERMEDIARIO/AVANCADO, ano INTEGER, arquivo_url VARCHAR(500), observacoes TEXT, criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP), mais `COMMENT ON` para tabela e cada coluna. | Ficheiro existe com DDL completo conforme 01-RESEARCH.md secção 3.2. Sem `IF NOT EXISTS`. Sem `DROP TABLE`. CHECK constraint com 3 valores exactos. |
| 2.3 | **Migrar contra PostgreSQL real** | Com PostgreSQL já a correr (Plano 1), executar `.\mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/partitura -Dflyway.user=partitura -Dflyway.password=partitura` OU `.\mvnw spring-boot:run` (que executa Flyway no arranque). Confirmar que a migration corre sem erros e a tabela `partituras` é criada (`docker exec partitura-db psql -U partitura -d partitura -c "\dt"`). | Flyway reporta `Successfully applied 1 migration`. Tabela `partituras` existe com todas as colunas e constraints. |

### Plano 3: application.properties (INFRA-03)

Configurar o ficheiro `application.properties` para ligação ao PostgreSQL, Flyway e logging.

| # | Task | Descrição | Critério de Aceitação |
|---|------|-----------|----------------------|
| 3.1 | **Substituir `application.properties`** | Escrever configuração completa no ficheiro `src/main/resources/application.properties` com: datasource PostgreSQL (url, username, password, driver), HikariCP pool (max 5, min 2, timeout 30s), JPA (PostgreSQLDialect, ddl-auto=validate, show-sql=true, open-in-view=false), Flyway (enabled=true, locations=classpath:db/migration, baseline-on-migrate=false), logging (DEBUG para app + hibernate.SQL + flywaydb, INFO para Spring + Hikari). | Ficheiro substituído com conteúdo exacto conforme 01-RESEARCH.md secção 2.2. |
| 3.2 | **Verificar arranque completo** | Executar `.\mvnw spring-boot:run` e confirmar que: (1) Flyway aplicou a V1 (ou "already up to date"), (2) Hibernate validou o schema (sem erros de `missing table` ou `missing column`), (3) HikariCP abriu conexões com sucesso, (4) Aplicação termina o arranque (Spring Boot started). | Logs mostram Flyway + Hibernate validate OK, app arranca sem erros. |

---

## 3. Ficheiros a Criar/Modificar

### Criar (3 ficheiros)

| Caminho Absoluto | Plano | Conteúdo |
|---|---|---|
| `C:\Users\rafael\Desktop\partitura-api\docker-compose.yml` | 1 | Docker Compose com PostgreSQL 16 (Alpine), healthcheck, volume nomeado |
| `C:\Users\rafael\Desktop\partitura-api\src\main\resources\db\migration\V1__criar_tabela_partitura.sql` | 2 | DDL completo da tabela `partituras` com 11 colunas, CHECK constraint, COMMENT ON |
| `C:\Users\rafael\Desktop\partitura-api\src\main\resources\application.properties` | 3 | Configuração completa de datasource, JPA, Flyway, logging |

### Modificar (0 ficheiros)

Nenhum ficheiro existente é modificado. O `application.properties` actual (com apenas `spring.application.name=partitura-api`) é **substituído** na sua totalidade.

### Estrutura final esperada

```
partitura-api/
├── docker-compose.yml                          ← NOVO
├── src/main/resources/
│   ├── application.properties                  ← SUBSTITUÍDO
│   └── db/migration/
│       └── V1__criar_tabela_partitura.sql      ← NOVO
└── ... (restante inalterado)
```

---

## 4. Ordem de Implementação

```
[1] Plano 1 (docker-compose.yml)
    ├── Task 1.1: Criar docker-compose.yml
    └── Task 1.3: docker compose up -d (verificar)

[2] Plano 2 (Migration Flyway V1)
    ├── Task 2.1: Criar directório db/migration/
    ├── Task 2.2: Criar V1__criar_tabela_partitura.sql
    └── ☣️ [BLOCKING] Task 2.3: flyway:migrate contra PostgreSQL real

[3] Plano 3 (application.properties)
    ├── Task 3.1: Substituir application.properties
    └── Task 3.2: spring-boot:run (verificar arranque completo)
```

**Justificação:** O PostgreSQL precisa de estar a correr (Plano 1) para testar a migration (Plano 2). O `application.properties` (Plano 3) depende de ambos para ser verificado ponta-a-ponta. Dentro de cada plano, as tarefas executam-se sequencialmente na ordem numerada.

### Dependências entre tarefas

```
Task 1.1 ──→ Task 1.3
                  │
                  ▼
             Task 2.1 ──→ Task 2.2 ──→ [BLOCKING] Task 2.3
                                                │
                                                ▼
                                           Task 3.1 ──→ Task 3.2
```

---

## 5. Critérios de Aceitação (ligados a Requirements)

### INFRA-01 — docker-compose.yml ✓

| # | Critério | Como verificar |
|---|----------|---------------|
| AC-01 | `docker-compose.yml` existe na raiz do projecto | `ls ./docker-compose.yml` |
| AC-02 | Imagem `postgres:16-alpine` | `head -20 docker-compose.yml` contém `image: postgres:16-alpine` |
| AC-03 | Container name `partitura-db` | `docker ps --filter name=partitura-db --format '{{.Names}}'` retorna `partitura-db` |
| AC-04 | Variáveis de ambiente `partitura` | `docker exec partitura-db printenv POSTGRES_DB POSTGRES_USER POSTGRES_PASSWORD` |
| AC-05 | Porta `5432:5432` | `docker port partitura-db 5432` retorna `0.0.0.0:5432` |
| AC-06 | Volume nomeado `pgdata` | `docker inspect partitura-db --format '{{json .Mounts}}'` inclui `pgdata` |
| AC-07 | Healthcheck `pg_isready` | `docker inspect partitura-db --format '{{json .State.Health}}'` mostra `"Status":"healthy"` |
| AC-08 | `restart: unless-stopped` | `docker inspect partitura-db --format '{{.HostConfig.RestartPolicy.Name}}'` retorna `unless-stopped` |

### INFRA-02 — Flyway Migration V1 ✓

| # | Critério | Como verificar |
|---|----------|---------------|
| AC-09 | Ficheiro `V1__criar_tabela_partitura.sql` existe em `db/migration/` | `ls src/main/resources/db/migration/V1__criar_tabela_partitura.sql` |
| AC-10 | Tabela criada com nome `partituras` (plural) | `\dt partituras` no PostgreSQL |
| AC-11 | Todas as 11 colunas presentes | `\d partituras` mostra: id, titulo, compositor, instrumento, genero, dificuldade, ano, arquivo_url, observacoes, criado_em, atualizado_em |
| AC-12 | CHECK constraint em `dificuldade` | `\d partituras` mostra constraint `chk_partituras_dificuldade` com valores `INICIANTE`, `INTERMEDIARIO`, `AVANCADO` |
| AC-13 | `COMMENT ON` para tabela e todas as colunas | `\dt+ partituras` + `\d+ partituras` mostra comentários |
| AC-14 | Migration executada sem erros | `docker exec partitura-db psql -U partitura -d partitura -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank"` mostra `1`, `criar_tabela_partitura`, `t` |
| AC-15 | Nenhuma tabela extra inesperada | Apenas `partituras` + `flyway_schema_history` existem |

### INFRA-03 — application.properties ✓

| # | Critério | Como verificar |
|---|----------|---------------|
| AC-16 | `spring.datasource.url=jdbc:postgresql://localhost:5432/partitura` | `grep datasource.url src/main/resources/application.properties` |
| AC-17 | `spring.jpa.hibernate.ddl-auto=validate` | `grep ddl-auto src/main/resources/application.properties` |
| AC-18 | `spring.jpa.show-sql=true` | `grep show-sql src/main/resources/application.properties` |
| AC-19 | `spring.jpa.open-in-view=false` | `grep open-in-view src/main/resources/application.properties` |
| AC-20 | `spring.flyway.enabled=true` | `grep flyway.enabled src/main/resources/application.properties` |
| AC-21 | Logging: INFO Spring, DEBUG SQL + Flyway | `grep logging.level src/main/resources/application.properties` |
| AC-22 | Aplicação arranca sem erros | `.\mvnw spring-boot:run` termina com "Started Application" sem `UnsatisfiedDependencyException` ou erros de conexão |

---

## 6. Passos de Verificação

### Verificação 1: Docker Compose (após Plano 1)

```powershell
# Subir contentor
docker compose up -d

# Aguardar healthcheck (poll até ficar healthy, timeout 30s)
for i in $(seq 1 30); do status=$(docker inspect -f '{{.State.Health.Status}}' partitura-db 2>/dev/null); if [ "$status" = "healthy" ]; then break; fi; sleep 1; done
docker ps --filter name=partitura-db --format "table {{.Names}}\t{{.Status}}"

# Verificar conexão
docker exec partitura-db pg_isready -U partitura -d partitura

# Logs de arranque
docker logs partitura-db --tail 20
```

Esperado: `Status: Up ... (healthy)`, `pg_isready` retorna `accepting connections`.

### Verificação 2: Flyway Migration (após Plano 2)

```powershell
# Executar Flyway via Maven (precisa de PostgreSQL a correr)
.\mvnw flyway:migrate

# OU: verificar migration já aplicada
docker exec partitura-db psql -U partitura -d partitura -c "SELECT version, description, installed_on, success FROM flyway_schema_history"

# Verificar estrutura da tabela
docker exec partitura-db psql -U partitura -d partitura -c "\d partituras"

# Verificar CHECK constraint
docker exec partitura-db psql -U partitura -d partitura -c "SELECT conname, consrc FROM pg_constraint WHERE conrelid = 'partituras'::regclass;"

# Verificar comentários
docker exec partitura-db psql -U partitura -d partitura -c "\dt+ partituras"
docker exec partitura-db psql -U partitura -d partitura -c "\d+ partituras"
```

Esperado: 1 migration applied successfully. Tabela com 11 colunas. CHECK constraint activa.

### Verificação 3: Arranque da Aplicação (após Plano 3)

```powershell
# Compilar e arrancar (Flyway + Hibernate validate executam durante o startup)
.\mvnw spring-boot:run

# Noutro terminal, verificar health
curl -s http://localhost:8080/actuator/health   # Se actuator presente
```

Esperado: Logs mostram:
```
Flyway: Successfully applied 1 migration
Hibernate: HHH000324: Found [explicitly] [valid] table [partituras]
HikariPool-1: Start completed.
Started Application in X.XXX seconds
```

Nenhum erro de conexão, nenhuma excepção.

### Verificação Final (Full Smoke Test)

```powershell
# 1. Verificar que tudo corre em simultâneo
docker compose down -v   # reset completo
docker compose up -d
.\mvnw spring-boot:run   # deve arrancar sem erros

# 2. Parar tudo (estado final para dev)
docker compose down      # pára contentor, volume mantém-se
```

---

## 7. Rollback & Resolução de Problemas

| Problema | Causa Provável | Solução |
|----------|---------------|---------|
| Porta 5432 ocupada | PostgreSQL nativo ou outro contentor | `docker compose down`; se outro processo: `netstat -ano \| findstr :5432` e parar. Alternativa: mudar porta no yml (não comitar). |
| `docker compose up` falha | Docker Desktop não instalado ou não a correr | Verificar `docker info`; iniciar Docker Desktop. |
| Flyway migration falha | Conexão recusada ou tabela já existe | Verificar healthcheck do PostgreSQL. Se tabela já existe: `docker compose down -v` para reset total. |
| Hibernate validation falha | Schema Java (Phase 2) não bate com migration | Corrigir entidade JPA ou migration — depende do erro. Durante esta fase não há entidades, por isso não deve acontecer. |
| `.\mvnw spring-boot:run` trava | PostgreSQL não disponível no arranque | Aguardar healthcheck. Ou aumentar `spring.datasource.hikari.initialization-fail-timeout=-1`. |

---

## 8. Resumo de Comandos

```powershell
# === Plano 1 ===
docker compose up -d
docker ps --filter name=partitura-db

# === Plano 2 ===
.\mvnw flyway:migrate

# === Plano 3 ===
.\mvnw spring-boot:run

# === Reset completo (volátil — apaga dados) ===
docker compose down -v
docker compose up -d

# === Parar (dados mantêm-se no volume) ===
docker compose down
```

---

## Appendix A: Checklist de Execução

- [ ] **Plano 1 — Task 1.1:** `docker-compose.yml` escrito com imagem 16-alpine, healthcheck, volume pgdata
- [ ] **Plano 1 — Task 1.3:** `docker compose up -d` → contentor healthy
- [ ] **Plano 2 — Task 2.1:** Directório `db/migration/` criado
- [ ] **Plano 2 — Task 2.2:** `V1__criar_tabela_partitura.sql` escrito com DDL completo
- [ ] **Plano 2 — ☣️ [BLOCKING] Task 2.3:** Migration executada contra PostgreSQL real → `Successfully applied 1 migration`
- [ ] **Plano 3 — Task 3.1:** `application.properties` substituído com config completa
- [ ] **Plano 3 — Task 3.2:** `.\mvnw spring-boot:run` → aplicação arranca sem erros
- [ ] **Verificação Final:** `docker compose down -v && docker compose up -d && .\mvnw spring-boot:run` → tudo OK de raiz

---

*Plan prepared: 2026-07-05*
*Next step: Execute via `gsd-execute-phase 1`*
