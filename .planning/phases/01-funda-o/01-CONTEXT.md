# Phase 1: Fundação - Context

**Gathered:** 2026-07-03
**Status:** Ready for planning

<domain>
## Phase Boundary

Esta fase entrega a infraestrutura base para desenvolvimento: PostgreSQL a correr via Docker, configuração do Spring Boot para ligar ao banco, e a migration Flyway inicial com o schema completo da tabela `partitura`. É uma fase puramente infraestrutural — sem endpoints REST, sem lógica de negócio, sem entidades JPA. Apenas o necessário para a Phase 2 começar a codificar sobre uma base sólida.

**Entrega:**
- `docker-compose.yml` com PostgreSQL 16, volume nomeado `pgdata`, credenciais `partitura/partitura`
- `application.properties` configurado com datasource PostgreSQL, Flyway, JPA validate, logging INFO + SQL debug
- `V1__criar_tabela_partitura.sql` — migration Flyway com schema completo (todos os campos, tipos, constraints)

</domain>

<decisions>
## Implementation Decisions

### DB Credenciais
- **D-01:** Nome da base, utilizador e password = `partitura` — simples e consistente com o nome do projeto. Fácil de lembrar em desenvolvimento local.

### Volume Docker
- **D-02:** Usar volume nomeado Docker (`pgdata`) para persistência dos dados PostgreSQL. Limpo, portátil, sem path absoluto. `docker-compose down -v` para reset completo.

### Logging
- **D-03:** Nível INFO para Spring, DEBUG para SQL e Flyway. Visibilidade das queries e migrations executadas sem ruído excessivo de outros componentes.

### JPA + Flyway
- **D-04:** `spring.jpa.hibernate.ddl-auto=validate` — o Flyway gere o schema, o Hibernate apenas valida que as entidades batem com as tabelas. `spring.jpa.show-sql=true` para debugging.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Domínio e Stack
- `docs/brief-partitura-api.md` — Stack obrigatória (Java 17, Spring Boot, PostgreSQL, Flyway), entidade Partitura com todos os campos, regras de implementação

### Planeamento
- `.planning/PROJECT.md` — Visão do projeto, constraints (Controller→Service→Repository, DTOs separados, testes obrigatórios)
- `.planning/ROADMAP.md` — Phase 1 é Fundação (3 plans: docker-compose, application.properties, migration Flyway)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **pom.xml**: Já inclui todas as dependências necessárias — Spring Web MVC, Spring Data JPA, Flyway Core, PostgreSQL Driver, Bean Validation, Lombok, JUnit 5. Não é necessário adicionar novas dependências nesta fase.
- **Spring Boot 4.1.0 + Java 17**: Stack já inicializada via Spring Initializr. Classe `Application` pronta.

### Established Patterns
- **Package**: `br.com.rocket.partitura` — namespace base já definido
- **application.properties**: Actualmente só tem `spring.application.name=partitura-api`. A config será expandida nesta fase.
- **Maven wrapper**: `./mvnw` incluso — comandos standard Maven

### Integration Points
- A configuração em `application.properties` será consumida pela Phase 2 (entidade JPA, repository, service)
- A migration `V1__criar_tabela_partitura.sql` define o schema que a entidade JPA da Phase 2 deve espelhar
- O `docker-compose.yml` é o ponto de entrada para qualquer dev — `docker compose up` e o banco está pronto

</code_context>

<specifics>
## Specific Ideas

Nenhuma — o utilizador não especificou preferências além das decisões capturadas acima. Abordagem standard para cada artefacto.

</specifics>

<deferred>
## Deferred Ideas

Nenhuma — a discussão manteve-se dentro do scope da Phase 1.

</deferred>

---

*Phase: 1-Fundação*
*Context gathered: 2026-07-03*
