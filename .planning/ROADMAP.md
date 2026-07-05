# Roadmap: Partitura API

**Created:** 2026-07-03
**Granularity:** Coarse (3 phases)
**Commit strategy:** Atomic per task

## Phase Overview

| Phase | Name | Plans | Requirements |
|-------|------|-------|-------------|
| 1 | Fundação | 3 | INFRA-01, INFRA-02, INFRA-03 |
| 2 | CRUD de Partituras | 4 | PART-01..05, VAL-01..02, ARCH-01..03 |
| 3 | Testes & Documentação | 2 | QUAL-01, QUAL-02 |

---

## Phase 1: Fundação

**Goal:** Projeto pronto para desenvolvimento — banco de dados configurado, migration inicial aplicada, docker-compose funcional.

### Plans

1. **docker-compose + PostgreSQL** — Criar `docker-compose.yml` com PostgreSQL 16, volume persistente, porta 5432
2. **application.properties** — Configurar datasource PostgreSQL, Flyway, e logging
3. **Migration Flyway V1** — Criar migration inicial `V1__criar_tabela_partitura.sql` com schema completo

**Canonical refs:**
- `docs/brief-partitura-api.md` — Stack obrigatória, entidade Partitura

---

## Phase 2: CRUD de Partituras

**Goal:** API REST completa com todos os 5 endpoints de partitura, validação, DTOs, e tratamento de erros.

### Plans

1. **Entidade + Repository** — Classe `Partitura` JPA com todos os campos, enum `Dificuldade`, `PartituraRepository`
2. **DTOs + Mapper** — `PartituraRequestDTO`, `PartituraResponseDTO`, mapper manual ou MapStruct
3. **Service** — `PartituraService` com lógica de CRUD, validações de negócio, exceções
4. **Controller + Exception Handler** — `PartituraController` com 5 endpoints REST, `GlobalExceptionHandler` para 404/400

**Canonical refs:**
- `docs/brief-partitura-api.md` — Endpoints, entidade, regras, DTOs

---

## Phase 3: Testes & Documentação

**Goal:** Cobertura de testes da camada de serviço e documentação completa para uso do projeto.

### Plans

1. **Testes unitários do Service** — Testes com Mockito para todos os métodos do `PartituraService` (CRUD completo, casos de borda, 404)
2. **README** — Instruções de como subir banco (docker-compose), rodar app (`./mvnw spring-boot:run`), e testar endpoints (curl)

**Canonical refs:**
- `docs/brief-partitura-api.md` — Testes unitários para service, README com instruções

---

## Execution Order

```
Phase 1 → Phase 2 → Phase 3
```

Linear. Cada fase depende da anterior.

---

## Next: `/gsd-plan-phase 1`

Run after this roadmap is approved to create a detailed PLAN.md for Phase 1.

---
*Last updated: 2026-07-03 after roadmap creation*
