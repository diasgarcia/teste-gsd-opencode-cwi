# 01-PLAN-CHECK.md — Goal-Backward Analysis do Phase 1 PLAN

**Data:** 2026-07-05
**Analisado por:** Goal-backward analysis (regras completas)
**Veridito:** ✅ **PASS** (com 3 FLAGS não bloqueantes)

---

## 1. Goal-Backward Trace

### Goal da fase (ROADMAP.md L19)

> "Projeto pronto para desenvolvimento — banco de dados configurado, migration inicial aplicada,
> docker-compose funcional."

### Decomposição do goal e rastreabilidade para tasks

| # | Deliverable implícito no goal | Task(s) que o entregam | Coberto? |
|---|-------------------------------|------------------------|----------|
| 1 | **docker-compose funcional** | Task 1.1 (criar ficheiro), Task 1.3 (subir e verificar healthy) | ✅ |
| 2 | **Migration inicial aplicada** (tabela `partituras` criada) | Task 2.1 (criar diretório), Task 2.2 (criar SQL), Task 2.3 (executar Flyway) | ✅ |
| 3 | **Banco de dados configurado** (Spring Boot liga ao PostgreSQL) | Task 3.1 (application.properties com datasource, JPA, Flyway, logging) | ✅ |
| 4 | **Projeto pronto para desenvolvimento** (arranque limpo) | Task 3.2 (spring-boot:run sem erros) | ✅ |

**Conclusão:** Todos os deliverables do goal têm tasks correspondentes. **Zero gaps de cobertura goal→task.**

---

## 2. Cobertura de Requirements

### INFRA-01 — docker-compose.yml

| Rastreabilidade | Evidência |
|---|---|
| **Task(s)** | Task 1.1 (criar docker-compose.yml), Task 1.3 (docker compose up -d) |
| **ACs** | AC-01 a AC-08 (8 critérios) |
| **Conteúdo validado contra** | RESEARCH.md secção 1.1 (docker-compose.yml exacto), CONTEXT.md D-01/D-02 |
| ✅ | Cobertura total |

### INFRA-02 — Flyway Migration V1

| Rastreabilidade | Evidência |
|---|---|
| **Task(s)** | Task 2.1 (criar diretório), Task 2.2 (criar `.sql`), Task 2.3 (flyway:migrate) |
| **ACs** | AC-09 a AC-15 (7 critérios) |
| **Conteúdo validado contra** | RESEARCH.md secção 3.2 (DDL completo), brief-partitura-api.md (entidade Partitura com 10+ campos) |
| ✅ | Cobertura total |

### INFRA-03 — application.properties

| Rastreabilidade | Evidência |
|---|---|
| **Task(s)** | Task 3.1 (substituir application.properties), Task 3.2 (verificar arranque) |
| **ACs** | AC-16 a AC-22 (7 critérios) |
| **Conteúdo validado contra** | RESEARCH.md secção 2.2 (properties exactas), CONTEXT.md D-03/D-04 |
| ✅ | Cobertura total |

**Conclusão:** INFRA-01, INFRA-02, INFRA-03 → cada um mapeado para ≥1 task e ≥1 AC. **Zero gaps de cobertura de requirements.**

---

## 3. Testabilidade dos Critérios de Aceitação

**Total de ACs:** 22 (AC-01 a AC-22)

Análise de cada AC quanto a verificabilidade concreta:

| AC | Verificação | Julgamento |
|----|-------------|------------|
| AC-01 | `ls ./docker-compose.yml` | ✅ Comando concreto |
| AC-02 | `head -20 docker-compose.yml` contém `image: postgres:16-alpine` | ✅ Comando + padrão esperado |
| AC-03 | `docker ps --filter name=partitura-db --format '{{.Names}}'` | ✅ Comando + output esperado |
| AC-04 | `docker exec partitura-db printenv ...` | ✅ Comando concreto |
| AC-05 | `docker port partitura-db 5432` | ✅ Comando concreto |
| AC-06 | `docker inspect ... --format '{{json .Mounts}}'` | ✅ Comando concreto |
| AC-07 | `docker inspect ... --format '{{json .State.Health}}'` | ✅ Comando concreto |
| AC-08 | `docker inspect ... --format '{{.HostConfig.RestartPolicy.Name}}'` | ✅ Comando concreto |
| AC-09 | `ls src/main/resources/db/migration/V1__criar_tabela_partitura.sql` | ✅ Comando concreto |
| AC-10 | `\dt partituras` no PostgreSQL | ✅ Comando concreto |
| AC-11 | `\d partituras` mostra 11 colunas | ✅ Comando + output esperado |
| AC-12 | `\d partituras` mostra `chk_partituras_dificuldade` | ✅ Comando concreto |
| AC-13 | `\dt+ partituras` + `\d+ partituras` | ✅ Comando concreto |
| AC-14 | Query `flyway_schema_history` | ✅ Comando concreto |
| AC-15 | Apenas `partituras` + `flyway_schema_history` | ✅ Observável |
| AC-16 | `grep datasource.url` | ✅ Comando concreto |
| AC-17 | `grep ddl-auto` | ✅ Comando concreto |
| AC-18 | `grep show-sql` | ✅ Comando concreto |
| AC-19 | `grep open-in-view` | ✅ Comando concreto |
| AC-20 | `grep flyway.enabled` | ✅ Comando concreto |
| AC-21 | `grep logging.level` | ✅ Comando concreto |
| AC-22 | `.\mvnw spring-boot:run` sem erros | ✅ Comando + output esperado |

**Conclusão:** 22/22 ACs são verificáveis. Nenhum AC vago como "funciona corretamente". **Zero ACs rejeitáveis.**

---

## 4. Grafo de Dependências

### Grafo declarado (PLAN secção 4)

```
Task 1.1 ──→ Task 1.3
                  │
                  ▼
             Task 2.1 ──→ Task 2.2 ──→ [BLOCKING] Task 2.3
                                                │
                                                ▼
                                           Task 3.1 ──→ Task 3.2
```

### Análise

| Verificação | Resultado |
|-------------|-----------|
| **Acíclico?** | ✅ Sim — grafo linear, sem ciclos |
| **Dependências lógicas corretas?** | ✅ Docker up antes de Flyway migrate; Flyway migrate antes de Spring Boot validate |
| **Task 3.1 depende realmente de Task 2.3?** | ⚠️ FLAG-1 — ver abaixo |
| **[BLOCKING] presente?** | ✅ Sim — Task 2.3 marcada como ☣️ [BLOCKING] |
| **[BLOCKING] bem posicionada?** | ✅ Entre Task 2.2 (criação SQL) e Task 3.2 (verificação Spring Boot) |

**FLAG-1 (não bloqueante):** Task 3.1 (escrever `application.properties`) é mostrada como dependente de Task 2.3 (flyway:migrate). Na prática, o ficheiro de properties pode ser escrito independentemente, antes ou em paralelo com Planos 1 e 2. A dependência real é de Task 3.2 (verificação spring-boot:run) sobre Task 2.3. O grafo atual força sequencialidade conservadora — **não está errado**, apenas é mais restritivo que o estritamente necessário. Isto não causa falhas de execução, apenas alonga ligeiramente o caminho crítico.

---

## 5. Verificação do Schema Push [BLOCKING]

| Critério | Situação |
|----------|----------|
| Task de schema push existe? | ✅ Task 2.3 — `flyway:migrate` contra PostgreSQL real |
| Marcada como bloqueante? | ✅ `☣️ [BLOCKING]` |
| Posicionada após criação do SQL? | ✅ Após Task 2.2 (criar `V1__criar_tabela_partitura.sql`) |
| Posicionada após PostgreSQL disponível? | ✅ Após Task 1.3 (docker compose up verificado healthy) |
| Posicionada antes da verificação final? | ✅ Antes de Task 3.2 (spring-boot:run) |
| Comando de execução especificado? | ✅ `.\mvnw flyway:migrate -Dflyway.url=...` OU `.\mvnw spring-boot:run` |
| Rollback documentado? | ✅ Tabela na secção 7 do PLAN cobre falhas de migration |

**Conclusão:** Schema push corretamente identificado e posicionado.

---

## 6. Análise de Completude — Potenciais Bloqueadores

### O que o PLAN cobre

| Área | Cobertura |
|------|-----------|
| Porta 5432 ocupada | ✅ Rollback table (secção 7): `netstat -ano`, mudar porta (não comitar) |
| Docker Desktop não instalado | ✅ Rollback table: `docker info` |
| Migration falha (conexão recusada) | ✅ Rollback table: verificar healthcheck, reset com `down -v` |
| Hibernate validation falha | ✅ Rollback table: embora improvável nesta fase |
| `spring-boot:run` trava | ✅ Rollback table: `initialization-fail-timeout=-1` como opção |
| Reset completo documentado | ✅ `docker compose down -v` + `up` |

### O que o PLAN NÃO cobre (ou cobre de forma imprecisa)

| # | Achado | Severidade | Detalhe |
|---|--------|------------|---------|
| **FLAG-2** | Comando `docker wait` mal usado nos passos de verificação | Baixa | L175: `docker wait partitura-db` bloqueia até o contentor **parar**, não até ficar healthy. O comando correto seria `docker compose up -d --wait` (Docker Compose V2) ou polling do healthcheck. Contudo, a Task 1.3 do PLAN descreve o procedimento correto (verificar `docker ps`). O erro está apenas nos passos de verificação ilustrativos, não na task executável. |
| **FLAG-3** | Referência a `RESEARCH.md` vs `01-RESEARCH.md` | Baixa | O PLAN referencia `RESEARCH.md secção 1.1`, `secção 2.2`, `secção 3.2`. O ficheiro real na diretoria é `01-RESEARCH.md`. O conteúdo das secções referenciadas existe e corresponde exatamente. Um agente ou humano que procure `RESEARCH.md` pode não encontrar o ficheiro à primeira. |

### O que está fora do scope do PLAN (e está correto)

- ✅ Nenhuma entidade JPA — corretamente adiado para Phase 2
- ✅ Nenhum endpoint REST — corretamente adiado para Phase 2
- ✅ Nenhum trigger SQL para `atualizado_em` — decisão documentada (usar `@PreUpdate` na Phase 2)
- ✅ Índices adicionais não incluídos — RESEARCH.md regista como "Phase 2+"
- ✅ Sem `.env` — Task 1.2 explicitamente skipped, decisão documentada

---

## 7. Alinhamento com ROADMAP.md

| Aspecto | ROADMAP | PLAN | Alinhado? |
|---------|---------|------|-----------|
| Número de plans | 3 | 3 | ✅ |
| Plan 1 | docker-compose + PostgreSQL | Plano 1: docker-compose + PostgreSQL | ✅ |
| Plan 2 | application.properties | Plano 3: application.properties | ⚠️ Ordem trocada |
| Plan 3 | Migration Flyway V1 | Plano 2: Migration Flyway V1 | ⚠️ Ordem trocada |
| Requirements | INFRA-01, INFRA-02, INFRA-03 | INFRA-01, INFRA-02, INFRA-03 | ✅ |
| Dependência entre fases | Phase 1 → Phase 2 | Respeitada | ✅ |

**Nota sobre a troca de ordem:** O ROADMAP lista `application.properties` como Plan 2 e `Migration Flyway V1` como Plan 3. O PLAN inverteu a ordem (migration = Plano 2, properties = Plano 3). A justificação (secção 4 do PLAN) é sólida: a migration precisa de ser executada contra PostgreSQL real antes da verificação do Spring Boot. A nova ordem é **mais correta** do ponto de vista de dependências técnicas. Não é um problema de alinhamento — é uma melhoria.

---

## 8. Alinhamento com CONTEXT.md

| Decisão no CONTEXT | Refletida no PLAN? |
|---|---|
| **D-01:** credenciais `partitura/partitura` | ✅ Task 1.1, Task 3.1 |
| **D-02:** volume nomeado `pgdata` | ✅ Task 1.1, AC-06 |
| **D-03:** logging INFO Spring + DEBUG SQL/Flyway | ✅ Task 3.1, AC-21 |
| **D-04:** `ddl-auto=validate`, Flyway gere schema | ✅ Task 3.1, AC-17 |
| Phase boundary: sem endpoints, sem entidades | ✅ Nenhuma task excede o boundary |

---

## 9. Alinhamento com brief-partitura-api.md

| Campo da entidade Partitura | Presente na V1 DDL? | AC associado |
|---|---|---|
| `id` (Long) → `BIGSERIAL` | ✅ | AC-11 |
| `titulo` (String, obrigatório) → `VARCHAR(255) NOT NULL` | ✅ | AC-11 |
| `compositor` (String, obrigatório) → `VARCHAR(255) NOT NULL` | ✅ | AC-11 |
| `instrumento` (String, obrigatório) → `VARCHAR(255) NOT NULL` | ✅ | AC-11 |
| `genero` (String, opcional) → `VARCHAR(100)` | ✅ | AC-11 |
| `dificuldade` (enum) → `VARCHAR(50) NOT NULL` + CHECK | ✅ | AC-12 |
| `ano` (Integer, opcional) → `INTEGER` | ✅ | AC-11 |
| `arquivoUrl` (String, opcional) → `arquivo_url VARCHAR(500)` | ✅ | AC-11 |
| `criadoEm` → `criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP` | ✅ | AC-11 |
| `atualizadoEm` → `atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP` | ✅ | AC-11 |

Todos os 10 campos lógicos (11 colunas físicas) da entidade Partitura estão cobertos na migration V1. Nenhum campo do brief ficou de fora.

---

## 10. Sumário de Achados

### Forças do PLAN

1. **Cobertura total:** 3 requirements → 3 plans → 9 tasks → 22 ACs. Sem gaps.
2. **ACs 100% testáveis:** Todos os 22 ACs têm comando concreto + output esperado.
3. **[BLOCKING] bem identificado:** Task 2.3 corretamente sinalizada e posicionada.
4. **Rollback e troubleshooting:** Tabela com 5 cenários de falha e soluções.
5. **Verificações em 3 níveis:** Por plano, integrada (spring-boot:run) e full smoke test.
6. **Alinhamento com fontes canónicas:** CONTEXT.md, RESEARCH.md, brief-partitura-api.md.
7. **Decisões pendentes resolvidas:** Nome da tabela (partituras), campos genero/ano, sem trigger.

### FLAGS (não bloqueantes)

| FLAG | Severidade | Descrição | Recomendação |
|------|------------|-----------|--------------|
| **FLAG-1** | ⚠️ Baixa | Task 3.1 (escrever application.properties) não precisa depender de Task 2.3 (flyway:migrate). O grafo força sequencialidade conservadora. | Opcional: separar Task 3.1 da dependência de Task 2.3. Criar properties em paralelo com Planos 1/2. Apenas Task 3.2 precisa esperar por Task 2.3. **Não é necessário corrigir para execução.** |
| **FLAG-2** | ⚠️ Baixa | `docker wait partitura-db` (L175) bloqueia até o contentor parar, não até ficar healthy. | Nos passos de verificação, substituir `docker wait` por polling do healthcheck ou `docker compose up -d --wait`. A Task 1.3 já descreve o procedimento correto. |
| **FLAG-3** | ⚠️ Baixa | PLAN referencia `RESEARCH.md` mas o ficheiro chama-se `01-RESEARCH.md`. | Corrigir as 3 referências no PLAN: `RESEARCH.md secção 1.1` → `01-RESEARCH.md secção 1.1`, etc. Ou renomear o ficheiro. |

### GAPS (bloqueantes)

**Nenhum.** O plano, se executado como escrito, entrega integralmente o goal da fase.

---

## 11. Veridito Final

| Critério | Resultado |
|----------|-----------|
| Goal → tasks (gap analysis) | ✅ PASS — 4 deliverables, 4 grupos de tasks |
| Requirements → tasks + ACs | ✅ PASS — INFRA-01,02,03 todos cobertos |
| ACs testáveis (22/22) | ✅ PASS — zero ACs vagos |
| Grafo de dependências acíclico | ✅ PASS — linear, sem ciclos |
| [BLOCKING] schema push | ✅ PASS — Task 2.3 corretamente identificada e posicionada |
| Completude (bloqueadores potenciais) | ✅ PASS — rollback cobre 5 cenários |
| Alinhamento ROADMAP, CONTEXT, brief | ✅ PASS |

### ✅ VERIDITO: PASS

O plano **Phase 1 — Fundação** está apto para execução. Se as 9 tasks forem implementadas na ordem especificada, o goal "Projeto pronto para desenvolvimento" será atingido. As 3 FLAGS são cosméticas ou de otimização — nenhuma impede a execução bem-sucedida.

---

*Análise concluída: 2026-07-05*
*Próximo passo: `gsd-execute-phase 1`*
