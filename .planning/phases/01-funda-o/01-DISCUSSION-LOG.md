# Phase 1: Fundação - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-03
**Phase:** 1-Fundação
**Areas discussed:** DB Credenciais, Volume Docker, Logging, JPA + Flyway

---

## DB Credenciais

| Option | Description | Selected |
|--------|-------------|----------|
| partitura / partitura / partitura | Simples e consistente — nome da base, user e password iguais ao nome do projeto | ✓ |
| partituradb / postgres / postgres | User/postgres padrão PostgreSQL, base com nome qualificado | |
| partituradb / partitura_user / partitura_pass | User e password específicos da aplicação, mais seguro mesmo em dev | |

**User's choice:** partitura / partitura / partitura
**Notes:** Simplicidade para desenvolvimento local. Em produção serão usadas credenciais diferentes via variáveis de ambiente.

---

## Volume Docker

| Option | Description | Selected |
|--------|-------------|----------|
| Volume nomeado (pgdata) | Volume Docker gerido — limpo, portátil, sem path absoluto | ✓ |
| Bind mount (./data/pgdata) | Pasta local no projeto — visível, fácil de inspecionar/apagar | |

**User's choice:** Volume nomeado (pgdata)
**Notes:** Abordagem standard Docker. `docker-compose down -v` para reset completo quando necessário.

---

## Logging

| Option | Description | Selected |
|--------|-------------|----------|
| INFO + SQL debug | INFO para Spring, DEBUG para SQL/Flyway | ✓ |
| DEBUG completo | DEBUG para tudo (Spring, JPA, Flyway, HikariPool) | |
| Só INFO | INFO para tudo. Limpo, menos visibilidade | |

**User's choice:** INFO + SQL debug
**Notes:** Balanço entre visibilidade útil (queries, migrations) e ruído controlado.

---

## JPA + Flyway

| Option | Description | Selected |
|--------|-------------|----------|
| ddl-auto=validate + show-sql | Flyway gere schema, Hibernate valida. SQL visível | ✓ |
| ddl-auto=none + sem show-sql | Flyway gere tudo, sem intervenção do Hibernate | |
| ddl-auto=update (só dev) | Hibernate atualiza schema automaticamente em dev | |

**User's choice:** ddl-auto=validate + show-sql
**Notes:** Abordagem canónica para produção. Flyway é a fonte de verdade do schema; Hibernate apenas confirma consistência.

---

## Deferred Ideas

Nenhuma — a discussão manteve-se dentro do scope da Phase 1.
